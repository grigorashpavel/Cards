package com.pasha.auth.internal.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pasha.auth.internal.domain.models.Credentials
import com.pasha.auth.internal.domain.repositories.AuthRepository

internal class AuthViewModel constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _authStateHolder: MutableLiveData<CredentialsState> by lazy {
        MutableLiveData<CredentialsState>()
    }
    val authStateHolder get(): LiveData<CredentialsState> = _authStateHolder


}