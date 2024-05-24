package com.pasha.auth.internal.presentation

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.pasha.auth.internal.domain.models.Credentials
import com.pasha.auth.internal.domain.repositories.AuthRepository
import com.pasha.core.network.api.utils.Response
import com.pasha.core.shared.SharedApplicationViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

private const val EMAIL_KEY = "EMAIL_KEY"
private const val PASSWORD_KEY = "PASSWORD_KEY"
private const val CODE_KEY = "CODE_KEY"

private const val VM_TAG = "AUTH_VIEW_MODEL"

internal class AuthViewModel(
    private val authRepository: AuthRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {
    private val _isAuthorized by lazy {
        MutableLiveData(false)
    }
    val isAuthorized: LiveData<Boolean> = _isAuthorized

    private val _authStateHolder: MutableLiveData<CredentialsState> by lazy {
        MutableLiveData<CredentialsState>()
    }
    val authStateHolder get(): LiveData<CredentialsState> = _authStateHolder

    private val _errorStateHolder: MutableLiveData<ErrorState> by lazy {
        MutableLiveData<ErrorState>()
    }
    val errorStateHolder get(): LiveData<ErrorState> = _errorStateHolder

    fun saveCredentials(email: String, password: String) {
        Log.d(VM_TAG, "fun saveCredentials(email: $email password: $password)")

        savedState[EMAIL_KEY] = email
        savedState[PASSWORD_KEY] = password
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var activeJob: Job? = null
    fun saveAffirmativeCode(code: String) {
        Log.d(VM_TAG, "fun saveAffirmativeCode(code: $code)")

        savedState[CODE_KEY] = code
    }

    fun signIn(email: String, password: String) {
        Log.d(
            VM_TAG,
            "fun signIn(email: $email, password: $password) Thread: ${Thread.currentThread().name}"
        )

        val credentials = Credentials(email, password)

        activeJob = authRepository.signIn(credentials).onEach { response ->
            if (coroutineContext.isActive.not()) return@onEach

            Log.d(VM_TAG, "fun signIn.onEach Thread ${Thread.currentThread().name}")

            when (response) {
                is Response.Loading -> {
                    _isLoading.value = true

                    Log.d(VM_TAG, "fun signIn: Response.Loading")
                }

                is Response.Success -> {
                    _isLoading.value = false
                    println("access: ${response.data.accessToken}")
                    println("refresh: ${response.data.refreshToken}")

                    _isAuthorized.value = true

                    Log.d(VM_TAG, "fun signIn: Response.Success: $response")
                }

                is Response.Error -> {
                    _isLoading.value = false

                    _errorStateHolder.value =
                        ErrorState(responseMessage = response.errorMessage)
                    Log.d(VM_TAG, "fun signIn: Response.Error: $response")
                }
            }

        }.launchIn(viewModelScope)
    }

    fun cancelLastTask() {
        _isLoading.value = false

        Log.d(VM_TAG, "$activeJob is cancelled now by fun cancelLastTask()")
        activeJob?.cancel()
        activeJob = null
    }

    fun signUp(email: String, password: String) {
        val credentials = Credentials(email, password)

        Log.d(
            VM_TAG,
            "fun signUp(email: $email, password: $password) Thread: ${Thread.currentThread().name}"
        )

        activeJob = authRepository.signUp(credentials).onEach { response ->
            if (coroutineContext.isActive.not()) return@onEach

            Log.d(VM_TAG, "fun signUp.onEach Thread ${Thread.currentThread().name}")

            when (response) {
                is Response.Loading -> {
                    _isLoading.value = true

                    Log.d(VM_TAG, "fun signUp: Response.Loading")
                }

                is Response.Success -> {
                    _isLoading.value = false

                    println(response.data.accessToken)
                    println(response.data.refreshToken)

                    _isAuthorized.value = true

                    Log.d(VM_TAG, "fun signUp: Response.Success: $response")
                }

                is Response.Error -> {
                    _isLoading.value = false

                    _errorStateHolder.value =
                        ErrorState(responseMessage = response.errorMessage)
                    Log.d(VM_TAG, "fun signUp: Response.Error: $response")
                }
            }

        }.launchIn(viewModelScope)

        activeJob?.invokeOnCompletion { exception ->
            if (exception == null) {
                activeJob = null
            } else {
                throw exception
            }
        }
    }

    fun clearErrorState() {
        _errorStateHolder.value = ErrorState()
    }

    fun restoreUiState() {
        val email = savedState.get<String>(EMAIL_KEY) ?: ""
        val password = savedState.get<String>(PASSWORD_KEY) ?: ""
        val code = savedState.get<String>(CODE_KEY) ?: ""

        Log.d(VM_TAG, "fun restoreUiState(email: $email, password: $password, code: $code)")

        _authStateHolder.value = CredentialsState(email, password, code)
    }

    class Factory @AssistedInject constructor(
        private val authRepository: AuthRepository,
        @Assisted("owner") private val owner: SavedStateRegistryOwner,
        @Assisted("args") private val defaultArgs: Bundle?
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return AuthViewModel(authRepository, handle) as T
        }

        @AssistedFactory
        interface Factory {
            fun create(
                @Assisted("owner") owner: SavedStateRegistryOwner,
                @Assisted("args") defaultArgs: Bundle? = null
            ): AuthViewModel.Factory
        }
    }
}