package com.pasha.edit.api

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.pasha.core.di.findDependencies
import com.pasha.core.progress_indicator.api.ProgressIndicator
import com.pasha.core.ui_deps.ActivityUiDeps
import com.pasha.edit.databinding.FragmentEditCardBinding
import com.pasha.edit.internal.di.DaggerEditComponent
import com.pasha.edit.internal.presentation.EditViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "EditCardFragment"

class EditCardFragment : Fragment() {
    private var _binding: FragmentEditCardBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextInputEditText>? = null

    @Inject
    internal lateinit var viewmodelFactory: EditViewModel.Factory
    private val viewModel: EditViewModel by activityViewModels<EditViewModel> { viewmodelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        DaggerEditComponent.factory()
            .create(findDependencies(), requireContext())
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fields = listOf(
            binding.etFamily, binding.etName, binding.etEmail, binding.etPhone, binding.etCardName
        )

        setListeners()

        viewModel.editableState.observe(viewLifecycleOwner) {
            viewModel.recreateVCard()
            viewModel.recreateUi()
        }

        viewModel.uiVCard.observe(viewLifecycleOwner) { codedCard ->
            binding.tvResponse.text = codedCard
        }

        val indicator = ProgressIndicator().apply {
            setCancelCallback {
                viewModel.cancelTask()
                viewModel.refreshEditState()
            }
        }
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            if (state.isLoading) {
                indicator.show(childFragmentManager, "${TAG}_Indicator")
            } else if (indicator.isResumed) {
                indicator.dismissAllowingStateLoss()
            }

            if (state.isUploaded) {
                (requireActivity() as ActivityUiDeps).showMessage(header = "Uploaded", "")
            } else if (state.errorMessage != null) {
                (requireActivity() as ActivityUiDeps).showMessage(message = state.errorMessage)
            }
        }

        setUploadListener()
    }

    private fun setListeners() {
        binding.etFamily.setupFocusChangeListener { newFamily ->
            viewModel.editableState.value = viewModel.editableState.value?.copy(family = newFamily)
        }
        binding.etName.setupFocusChangeListener { newName ->
            viewModel.editableState.value = viewModel.editableState.value?.copy(name = newName)
        }
        binding.etEmail.setupFocusChangeListener { newEmail ->
            viewModel.editableState.value = viewModel.editableState.value?.copy(email = newEmail)
        }
        binding.etPhone.setupFocusChangeListener { newPhone ->
            viewModel.editableState.value = viewModel.editableState.value?.copy(phone = newPhone)
        }
    }

    private fun setUploadListener() {
        binding.fabUpload.setOnClickListener {
            checkForEmptyAndSetError()

            val errorFound = fields?.find { it.error != null } != null
            if (fields == null || errorFound) {
                checkForEmptyAndSetError()

                return@setOnClickListener
            }

            viewModel.uploadVCard(binding.etCardName.text.toString())

            val handler = Handler(Looper.getMainLooper())
            val taskStateUpdate = Runnable {
                viewModel.refreshEditState()
            }
            handler.postDelayed(taskStateUpdate, 2000)
        }
    }

    private fun checkForEmptyAndSetError() {
        if (fields == null) return

        for (field in fields!!) {
            val itIncorrect = field.text.toString().length < 3
            if (itIncorrect) {
                field.error = "Field Must be longer than 3 symbols!"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fields = null
    }

    private fun TextInputEditText.setupFocusChangeListener(saveText: (String) -> Unit) {
        onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                saveText(text.toString())
            }
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (error != null) error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}