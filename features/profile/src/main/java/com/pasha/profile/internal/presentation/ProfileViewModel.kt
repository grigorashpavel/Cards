package com.pasha.profile.internal.presentation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.savedstate.SavedStateRegistryOwner
import com.pasha.core.network.api.utils.Response
import com.pasha.profile.internal.domain.repositories.ProfileRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.coroutines.coroutineContext


private const val VM_TAG = "ProfileViewModel"

internal class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    init {
        Log.d(VM_TAG, "VM created()")
    }

    private val _profileStateHolder by lazy { MutableLiveData(ProfileState()) }
    val profileStateHolder: LiveData<ProfileState> get() = _profileStateHolder

    var editableState = EditableState()
        private set


    private var lastTask: Job? = null

    fun cancelTask() {
        lastTask?.cancel()
        lastTask = null

        _profileStateHolder.value = _profileStateHolder.value?.copy(isLoading = false)
    }

    fun saveImageUri(uri: Uri?) {
        _profileStateHolder.value = _profileStateHolder.value?.copy(imageUri = uri)
    }

    fun startActionMode() {
        _profileStateHolder.value = _profileStateHolder.value?.copy(isAction = true)
    }

    fun setEditableData(uri: Uri? = null, username: String? = null) {
        if (uri != null) editableState = editableState.copy(imageUri = uri)
        if (username != null) editableState = editableState.copy(username = username)
    }

    fun stopActionMode() {
        _profileStateHolder.value = _profileStateHolder.value?.copy(isAction = false)
    }

    fun fetchProfile() {
        lastTask = profileRepository.getProfile().onEach { response ->
            if (coroutineContext.isActive.not()) return@onEach

            when (response) {
                is Response.Loading -> {
                    _profileStateHolder.value =
                        _profileStateHolder.value?.copy(isLoading = true)
                }

                is Response.Error -> {
                    _profileStateHolder.value = _profileStateHolder.value?.copy(
                        isLoading = false,
                        errorMessage = "Code: ${response.code}\nMessage: ${response.errorMessage}"
                    )
                    _profileStateHolder.value = _profileStateHolder.value?.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                }

                is Response.Success -> {
                    _profileStateHolder.value = _profileStateHolder.value?.copy(
                        isLoading = false,
                        errorMessage = null,

                        email = response.data.email,
                        username = response.data.username,
                        avatarPath = response.data.avatarPath,
                        headerPath = response.data.headerPath
                    )
                }
            }

        }.launchIn(viewModelScope)
    }

    class Factory @Inject constructor(
        private val profileRepository: ProfileRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(profileRepository) as T
        }
    }
}