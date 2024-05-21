package com.pasha.auth.internal.data.repositories

import com.pasha.auth.internal.data.AuthService
import com.pasha.auth.internal.domain.models.Credentials
import com.pasha.auth.internal.domain.models.Tokens
import com.pasha.auth.internal.domain.repositories.AuthRepository
import com.pasha.core.network.api.utils.AuthResponse
import com.pasha.core.network.api.utils.requestTokensFlow
import com.pasha.core.network.internal.models.CredentialsDto
import com.pasha.core.network.internal.models.TokensDto
import com.pasha.core.store.api.IdentificationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AuthRepositoryRetrofitImpl @Inject constructor(
    private val authService: AuthService,
    private val identificationManager: IdentificationManager
) : AuthRepository {
    override fun signIn(credentials: Credentials): Flow<AuthResponse<Tokens>> {
        val credentialsDto = CredentialsDto(
            deviceId = identificationManager.getAndroidId(),
            email = credentials.email,
            password = credentials.password
        )

        return requestTokensFlow {
            authService.signIn(credentialsDto)
        }.asTokensFlow()
    }

    override fun signUp(credentials: Credentials): Flow<AuthResponse<Tokens>> {
        val credentialsDto = CredentialsDto(
            deviceId = identificationManager.getAndroidId(),
            email = credentials.email,
            password = credentials.password
        )

        return requestTokensFlow {
            authService.signUp(credentialsDto)
        }.asTokensFlow()
    }

    private fun Flow<AuthResponse<TokensDto>>.asTokensFlow(): Flow<AuthResponse<Tokens>> =
        map { response ->
            when (response) {
                is AuthResponse.Success -> AuthResponse.Success(
                    Tokens(
                        accessToken = response.data.accessToken,
                        refreshToken = response.data.refreshToken
                    )
                )

                is AuthResponse.Loading -> AuthResponse.Loading
                is AuthResponse.Error -> AuthResponse.Error(response.code, response.errorMessage)
            }
        }
}