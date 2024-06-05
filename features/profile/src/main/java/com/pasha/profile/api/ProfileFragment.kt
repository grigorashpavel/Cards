package com.pasha.profile.api

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.pasha.core.di.findDependencies
import com.pasha.core.network.api.NetworkUtil
import com.pasha.core.progress_indicator.api.ProgressIndicator
import com.pasha.core.ui_deps.ActivityUiDeps
import com.pasha.profile.R
import com.pasha.profile.databinding.FragmentProfileBinding
import com.pasha.profile.internal.data.UploadService
import com.pasha.profile.internal.di.DaggerProfileComponent
import com.pasha.profile.internal.presentation.ProfileState
import com.pasha.profile.internal.presentation.ProfileViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.lang.Exception

import javax.inject.Inject


private const val PROFILE_TAG = "ProfileFragment"

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewmodelFactory: ProfileViewModel.Factory
    private val viewModel: ProfileViewModel by viewModels { viewmodelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        DaggerProfileComponent.factory()
            .create(findDependencies(), requireContext())
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isFirstLoading = savedInstanceState == null
        if (isFirstLoading) viewModel.fetchProfile()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var actionMode: ActionMode? = null
    private var pickerLauncher: ActivityResultLauncher<String>? = null
    private lateinit var actionModeBackPressedCallback: OnBackPressedCallback


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.groupProfileContent.visibility = View.GONE

        val indicator = ProgressIndicator()
        indicator.setCancelCallback(viewModel::cancelTask)
        viewModel.profileStateHolder.observe(viewLifecycleOwner) { state ->
            if (state.isLoading) {
                binding.progressIndicator.visibility = View.VISIBLE
            } else {
                binding.progressIndicator.visibility = View.GONE
            }

            if (state.errorMessage != null) {
                (requireActivity() as ActivityUiDeps).showErrorMessage(state.errorMessage)
            } else if (state.email.isNotEmpty() || state.username.isNotEmpty()) {
                binding.groupProfileContent.visibility = View.VISIBLE
                setCorrectState(state)
            }
        }

        createBackPressedCallback(callback = { actionMode?.finish() })

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, actionModeBackPressedCallback
        )

        binding.toolbar.setNavigationOnClickListener {
            actionMode?.finish()
        }

        registerImagePicker()

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.settings -> {
                    findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
                    true
                }

                R.id.edit -> {
                    startActionMode()
                    true
                }

                else -> false
            }
        }
    }

    private fun startActionMode() {
        val imagePickerListener = OnClickListener {
            pickerLauncher?.launch("image/*")
        }

        val imageFilter = binding.ivBackgroundHeader.colorFilter
        val actionCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                binding.ivBackgroundHeader.setColorFilter(
                    resources.getColor(
                        com.pasha.core_ui.R.color.primary_300,
                        requireActivity().theme
                    )
                )

                binding.collapsingBar.setBackgroundColor(
                    resources.getColor(
                        com.pasha.core_ui.R.color.primary_100, requireActivity().theme
                    )
                )
                binding.toolbar.setNavigationIcon(R.drawable.ic_close_24)
                binding.toolbar.setNavigationIconTint(
                    resources.getColor(
                        com.pasha.core_ui.R.color.primary_900, requireActivity().theme
                    )
                )
                actionModeBackPressedCallback.isEnabled = true

                binding.ivProfileImage.isClickable = true
                binding.ivProfileImage.setOnClickListener(imagePickerListener)

                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                binding.ivBackgroundHeader.colorFilter = imageFilter
                binding.collapsingBar.setBackgroundColor(
                    resources.getColor(
                        com.pasha.core_ui.R.color.primary_400, requireActivity().theme
                    )
                )
                actionModeBackPressedCallback.isEnabled = false
                binding.toolbar.navigationIcon = null

                binding.ivProfileImage.isClickable = false
                binding.ivProfileImage.setOnClickListener(null)
            }

        }

        actionMode = requireActivity().startActionMode(
            actionCallback, ActionMode.TYPE_FLOATING
        )
    }

    private fun createBackPressedCallback(callback: () -> Unit) {
        actionModeBackPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                callback.invoke()
            }
        }
    }

    private fun registerImagePicker() {
        pickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val uploadIntent = Intent(requireContext(), UploadService::class.java).apply {
                    putExtra(UploadService.IMAGE_URI_KEY, uri)
                }
                requireContext().startService(uploadIntent)

                binding.ivProfileImage.setImageURI(uri)
            }
        }
    }

    private fun setCorrectState(state: ProfileState) {
        Log.d(PROFILE_TAG, "StateChanged")

        if (state.headerPath.isNotEmpty()) {
            val path = NetworkUtil.BASE_ADDRESS + state.headerPath.removePrefix("/")
            Log.d(PROFILE_TAG, "PATH: $path")

            Picasso.get()
                .load(path)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(binding.ivBackgroundHeader, object : Callback {
                    override fun onSuccess() {
                        binding.ivBackgroundHeader.scaleType = ImageView.ScaleType.CENTER_CROP
                        binding.ivBackgroundHeader.setColorFilter(Color.TRANSPARENT)
                    }

                    override fun onError(e: Exception?) {}

                })



            binding.tvUsername.text = "Username: ${state.username}"
            binding.tvEmail.text = "Email: ${state.email}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        pickerLauncher = null
        actionMode = null
    }

}