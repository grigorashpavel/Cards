package com.pasha.auth.internal.domain.repositories

import com.pasha.auth.internal.domain.models.Credentials
import com.pasha.auth.internal.domain.models.Tokens
import com.pasha.core.network.api.utils.AuthResponse
import com.pasha.core.network.internal.models.CredentialsDto
import kotlinx.coroutines.flow.Flow


internal interface AuthRepository {
    fun signIn(credentials: Credentials): Flow<AuthResponse<Tokens>>
    fun signUp(credentials: Credentials): Flow<AuthResponse<Tokens>>
}