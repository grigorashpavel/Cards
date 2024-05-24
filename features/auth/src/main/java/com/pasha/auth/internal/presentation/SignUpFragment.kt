package com.pasha.auth.internal.presentation

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pasha.auth.api.AuthNavCommandProvider
import com.pasha.auth.databinding.FragmentSignUpBinding
import com.pasha.auth.internal.di.DaggerAuthComponent
import com.pasha.core.di.findDependencies
import com.pasha.core.progress_indicator.api.ProgressIndicator
import com.pasha.core_ui.R
import javax.inject.Inject


private const val TAG = "SignUpFragment"

internal class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    @Inject
    internal lateinit var factory: AuthViewModel.Factory.Factory

    @Inject
    lateinit var navigationProvider: AuthNavCommandProvider

    private val viewModel: AuthViewModel by viewModels {
        factory.create(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerAuthComponent
            .factory()
            .create(findDependencies())
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) viewModel.restoreUiState()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.footer.movementMethod = LinkMovementMethod.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isAuthorized.observe(viewLifecycleOwner) { authorized ->
            if (authorized) findNavController().navigate(navigationProvider.toAllCards.action)
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(com.pasha.auth.R.id.action_signUpFragment_to_signInFragment)
        }

        viewModel.authStateHolder.observe(viewLifecycleOwner) { state ->
            binding.etEmail.setText(state.email)
            binding.etPassword.setText(state.password)
        }

        viewModel.errorStateHolder.observe(viewLifecycleOwner) { state ->
            if (state.responseMessage != null) {
                showErrorMessage(state.responseMessage)
            }
        }

        binding.btnSignUp.setOnClickListener {
            viewModel.signUp(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }


        val progressIndicator = ProgressIndicator().apply {
            setCallback {
                viewModel.cancelLastTask()
                Toast.makeText(this@SignUpFragment.context, "Cancelled", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressIndicator.show(childFragmentManager, null)
            } else if (!isLoading && progressIndicator.isResumed) {
                progressIndicator.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        viewModel.saveCredentials(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        )

        super.onStop()
    }

    private fun showErrorMessage(message: String) {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.Theme_Pasha_MaterialAlertDialog_Centered
        )
            .setTitle("Ошибка")
            .setMessage(message)
            .setNeutralButton(android.R.string.ok) { _, _ ->
                viewModel.clearErrorState()
            }
            .show()

    }
}