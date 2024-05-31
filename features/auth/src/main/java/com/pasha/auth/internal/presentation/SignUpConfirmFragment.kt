package com.pasha.auth.internal.presentation

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pasha.auth.R
import com.pasha.auth.api.AuthNavCommandProvider
import com.pasha.auth.databinding.FragmentSignUpBinding
import com.pasha.auth.databinding.FragmentSignUpConfirmBinding
import com.pasha.auth.internal.di.DaggerAuthComponent
import com.pasha.core.di.findDependencies
import com.pasha.core.progress_indicator.api.ProgressIndicator
import com.pasha.core.ui_deps.ActivityUiDeps
import javax.inject.Inject


internal class SignUpConfirmFragment : Fragment() {
    private var _binding: FragmentSignUpConfirmBinding? = null
    private val binding get() = _binding!!

    @Inject
    internal lateinit var factory: AuthViewModel.Factory.Factory

    @Inject
    lateinit var navigationProvider: AuthNavCommandProvider

    private val viewModel: AuthViewModel by viewModels {
        factory.create(this)
    }

    private lateinit var uiDepsProvider: ActivityUiDeps

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerAuthComponent
            .factory()
            .create(findDependencies())
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uiDepsProvider = requireContext() as ActivityUiDeps
        if (savedInstanceState != null) viewModel.restoreUiState()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpConfirmBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiDepsProvider.hideBottomNavigationView()

        binding.btnConfirm.setOnClickListener {
            findNavController().navigate(navigationProvider.toAllCards.action)
        }

        viewModel.authStateHolder.observe(viewLifecycleOwner) { data ->
            binding.etCode.setText(data.confirmCode)
        }

        val progressIndicator = ProgressIndicator().apply {
            setCallback {
                viewModel.cancelLastTask()
                Toast.makeText(this@SignUpConfirmFragment.context, "Cancelled", Toast.LENGTH_SHORT)
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

    override fun onStop() {
        super.onStop()

        viewModel.saveAffirmativeCode(binding.etCode.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}