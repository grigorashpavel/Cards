package com.pasha.profile.api

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.navigation.fragment.findNavController
import com.pasha.core.di.findDependencies
import com.pasha.core.network.api.NetworkUtil
import com.pasha.core.ui_deps.ActivityUiDeps
import com.pasha.profile.R
import com.pasha.profile.databinding.FragmentProfileBinding
import com.pasha.profile.internal.data.UploadService
import com.pasha.profile.internal.di.DaggerProfileComponent
import com.pasha.profile.internal.presentation.ProfileState
import com.pasha.profile.internal.presentation.ProfileViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.Locale.Category

import javax.inject.Inject


private const val PROFILE_TAG = "ProfileFragment"

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    internal lateinit var authPicasso: Picasso

    @Inject
    internal lateinit var viewmodelFactory: ProfileViewModel.Factory
    private val viewModel: ProfileViewModel by viewModels { viewmodelFactory }
    private lateinit var uiProvider: ActivityUiDeps

    private lateinit var uploadReceiver: Receiver


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


    override fun onStart() {
        super.onStart()

        uploadReceiver = Receiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(UploadService.TRIGGER)
        requireActivity().registerReceiver(uploadReceiver, intentFilter)
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

        binding.refreshLayout.setColorSchemeColors(
            resources.getColor(com.pasha.core_ui.R.color.primary_400, requireActivity().theme)
        )
        binding.refreshLayout.setOnRefreshListener {
            viewModel.fetchProfile()
            binding.refreshLayout.isRefreshing = false
            binding.refreshLayout.isEnabled = false
            CoroutineScope(Dispatchers.Default).launch {
                delay(5000)
                activity?.runOnUiThread {
                    _binding?.refreshLayout?.isEnabled = true
                }
            }
        }

        val imageUri = viewModel.profileStateHolder.value?.imageUri
        if (imageUri != null) {
            binding.ivProfileImage.setImageURI(imageUri)
        }

        uiProvider = requireActivity() as ActivityUiDeps

        binding.groupProfileContent.visibility = View.GONE

        viewModel.profileStateHolder.observe(viewLifecycleOwner) { state ->
            if (state.isLoading) {
                binding.progressIndicator.visibility = View.VISIBLE
            } else {
                binding.progressIndicator.visibility = View.GONE
            }

            if (state.errorMessage != null) {
                val uiDeps = (requireActivity() as ActivityUiDeps)
                uiDeps.checkForAuthNavigation(state.errorMessage)
                uiDeps.showErrorMessage(state.errorMessage)
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
            viewModel.stopActionMode()
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
                    viewModel.startActionMode()
                    true
                }

                R.id.correct -> {
                    viewModel.setEditableData(username = binding.etUsername.text.toString())
                    actionMode?.finish()

                    val canEdit =
                        viewModel.editableState.imageUri != null || viewModel.editableState.username != null
                    if (canEdit) {
                        val uploadIntent =
                            Intent(requireContext(), UploadService::class.java).apply {
                                putExtra(
                                    UploadService.IMAGE_URI_KEY,
                                    viewModel.editableState.imageUri.toString()
                                )
                                putExtra(
                                    UploadService.USERNAME_KEY,
                                    viewModel.editableState.username
                                )
                            }
                        requireContext().startService(uploadIntent)
                    }
                    binding.ivProfileImage.setImageURI(viewModel.editableState.imageUri)
                    viewModel.stopActionMode()

                    true
                }

                else -> false
            }
        }

        val isRecreated = savedInstanceState != null
        val wasAction = viewModel.profileStateHolder.value?.isAction == true
        if (isRecreated && wasAction) {
            startActionMode()
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
                        com.pasha.core_ui.R.color.primary_100,
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
                binding.toolbar.menu.findItem(R.id.correct).isVisible = true

                actionModeBackPressedCallback.isEnabled = true

                binding.ivProfileImage.isClickable = true
                binding.ivProfileImage.setOnClickListener(imagePickerListener)
                onEditUsernameUi()

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
                binding.toolbar.menu.findItem(R.id.correct).isVisible = false

                binding.ivProfileImage.isClickable = false
                binding.ivProfileImage.setOnClickListener(null)
                offEditUsernameUi()
            }

        }

        actionMode = requireActivity().startActionMode(
            actionCallback, ActionMode.TYPE_FLOATING
        )
    }

    override fun onStop() {
        super.onStop()

        requireActivity().unregisterReceiver(uploadReceiver)
    }

    private fun onEditUsernameUi() {
        if (binding.groupProfileContent.visibility == View.GONE) return

        binding.etUsername.isEnabled = true
    }

    private fun offEditUsernameUi() {
        if (binding.groupProfileContent.visibility == View.GONE) return

        binding.etUsername.isEnabled = false
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
                viewModel.setEditableData(uri)
                binding.ivProfileImage.setImageURI(uri)
                viewModel.saveImageUri(uri)
            }
        }
    }

    private fun setCorrectState(state: ProfileState) {
        Log.d(PROFILE_TAG, "StateChanged")

        if (state.headerPath.isNotEmpty() && viewModel.profileStateHolder.value?.isAction != true) {
            val path = NetworkUtil.BASE_ADDRESS + state.headerPath.removePrefix("/")
            Log.d(PROFILE_TAG, "PATH: $path")
            val profilePath = NetworkUtil.BASE_ADDRESS + state.avatarPath.removePrefix("/")
            Log.d(PROFILE_TAG, "PATH: $profilePath")

            Picasso.get()
                .load(path)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(binding.ivBackgroundHeader, object : Callback {
                    override fun onSuccess() {
                        _binding?.ivBackgroundHeader?.scaleType = ImageView.ScaleType.CENTER_CROP
                        _binding?.ivBackgroundHeader?.setColorFilter(Color.TRANSPARENT)
                    }

                    override fun onError(e: Exception?) {}

                })

            authPicasso
                .load(profilePath)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(binding.ivProfileImage, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                    }

                })


            binding.etUsername.setText("${state.username}")
            binding.tvEmail.text = "Email: ${state.email}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        pickerLauncher = null
        actionMode = null
    }

    inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(PROFILE_TAG, "Reciever: onReceive()")
            val uploadSuccess = intent?.getBooleanExtra(UploadService.RESULT_KEY, true)
            if (uploadSuccess == true)
                viewModel.fetchProfile()
        }

    }
}