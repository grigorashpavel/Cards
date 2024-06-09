package com.pasha.all_cards.internal.presentation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.pasha.all_cards.databinding.FragmentViewerBinding
import com.pasha.all_cards.internal.di.DaggerAllCardsComponent
import com.pasha.core.di.findDependencies
import com.pasha.core.progress_indicator.api.ProgressIndicator
import com.pasha.core.ui_deps.ActivityUiDeps
import java.util.UUID
import javax.inject.Inject

private const val TAG = "ViewerFragment"

class ViewerFragment : Fragment() {
    private var _binding: FragmentViewerBinding? = null
    private val binding get() =  _binding!!

    @Inject
    internal lateinit var viewmodelFactory: ViewerViewModel.Factory
    private val viewModel: ViewerViewModel by activityViewModels<ViewerViewModel> { viewmodelFactory }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerAllCardsComponent.factory()
            .create(findDependencies())
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.cardData.observe(viewLifecycleOwner) { state ->
            binding.etFamily.text = state.family
            binding.etName.text = state.name
            binding.etEmail.text = state.email
            binding.etPhone.text = state.phone
        }

        val indicator = ProgressIndicator()
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            if (state.isLoading) {
                indicator.show(parentFragmentManager, "${TAG}_Indicator")
            } else if (indicator.isResumed) {
                indicator.dismiss()
            }

            if (state.errorMessage != null) {
                (requireActivity() as ActivityUiDeps).showMessage(message = state.errorMessage)
                viewModel.clearUiState()
            }
        }

        binding.fabRemove.setOnClickListener {
            viewModel.removeCard()
            viewModel.isDeleted.observe(viewLifecycleOwner) {
                if (it) findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}