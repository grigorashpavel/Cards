package com.pasha.auth.api

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pasha.auth.R
import com.pasha.auth.databinding.FragmentSignInBinding
import com.pasha.auth.internal.di.AuthComponent
import com.pasha.auth.internal.di.DaggerAuthComponent
import com.pasha.auth.internal.di.InternalAuthModule
import com.pasha.auth.internal.presentation.AuthViewModel
import com.pasha.core.di.findDependencies
import com.pasha.core.progress_indicator.api.ProgressIndicator
import javax.inject.Inject


class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
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
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isAuthorized.observe(viewLifecycleOwner) { authorized ->
            if (authorized) findNavController().navigate(navigationProvider.toAllCards.action)
        }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.btnSkip.setOnClickListener {
            findNavController().navigate(navigationProvider.toAllCards.action)
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

        val progressIndicator = ProgressIndicator().apply {
            setCallback(viewModel::cancelLastTask)
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

    private fun showErrorMessage(message: String) {
        MaterialAlertDialogBuilder(
            requireContext(),
            com.pasha.core_ui.R.style.Theme_Pasha_MaterialAlertDialog_Centered
        )
            .setTitle("Ошибка")
            .setMessage(message)
            .setNeutralButton(android.R.string.ok) { _, _ ->
                viewModel.clearErrorState()
            }
            .show()

    }
}
