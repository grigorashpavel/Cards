package com.pasha.auth.api

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pasha.auth.R
import com.pasha.auth.databinding.FragmentSignInBinding
import com.pasha.auth.internal.di.DaggerAuthComponent
import com.pasha.auth.internal.presentation.AuthViewModel
import com.pasha.core.account.CardsAccountManager
import com.pasha.core.di.findDependencies
import com.pasha.core.progress_indicator.api.ProgressIndicator
import com.pasha.core.ui_deps.ActivityUiDeps
import javax.inject.Inject


class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    @Inject
    internal lateinit var factory: AuthViewModel.Factory.Factory

    @Inject
    lateinit var navigationProvider: AuthNavCommandProvider

    private lateinit var uiDepsProvider: ActivityUiDeps

    private val viewModel: AuthViewModel by viewModels {
        factory.create(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerAuthComponent.factory()
            .create(findDependencies())
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiDepsProvider = requireContext() as ActivityUiDeps

        if (uiDepsProvider.isOpenedByAuthenticatorToCreateAccount()) {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        if (savedInstanceState != null) viewModel.restoreUiState()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiDepsProvider.hideBottomNavigationView()

        viewModel.isAuthorized.observe(viewLifecycleOwner) { isAuthorized ->
            if (isAuthorized) {
                val manager = CardsAccountManager(requireContext())
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.toString()

                val targetAccount = manager.cardsAccounts.find { it.name == email }
                if (targetAccount == null) {
                    val isCreated = manager
                        .createAccount(email, password, isTemporary = true, isActive = true)

                    if (isCreated) {
                        val tokens = viewModel.tokens
                        if (tokens != null) {
                            manager.setTokensOnActiveAccount(
                                tokens.accessToken,
                                tokens.refreshToken
                            )
                        }

                        uiDepsProvider.offerToAddAccount {
                            val createdAccount = manager.activeAccount
                            manager.saveAccount(createdAccount)
                        }

                    } else uiDepsProvider.showErrorMessage("Can`t create Account: $email")

                } else if (viewModel.tokens != null) {
                    manager.activateAccount(targetAccount)

                    val tokens = viewModel.tokens!!
                    manager.setTokensOnActiveAccount(tokens.accessToken, tokens.refreshToken)
                }

                findNavController().navigate(navigationProvider.toAllCards.action)
            }

        }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.btnSkip.setOnClickListener {
            findNavController().navigate(
                navigationProvider.toAllCards.action,
                null,
                navigationProvider.toAllCards.navOptions
            )
        }


        viewModel.authStateHolder.observe(viewLifecycleOwner) { state ->
            binding.etEmail.setText(state.email)
            binding.etPassword.setText(state.password)
        }

        viewModel.errorStateHolder.observe(viewLifecycleOwner) { state ->
            if (state.responseMessage != null) {
                uiDepsProvider.showErrorMessage(state.responseMessage)
            }
        }

        val progressIndicator = ProgressIndicator().apply {
            setCancelCallback(viewModel::cancelLastTask)
        }

        binding.btnLogin.setOnClickListener {
            viewModel.signIn(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressIndicator.show(childFragmentManager, null)
            } else if (!isLoading && progressIndicator.isResumed) {
                progressIndicator.dismiss()
            }
        }
    }

    override fun onStop() {
        viewModel.saveCredentials(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        )

        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
