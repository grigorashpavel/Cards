package com.pasha.auth.internal.data.repositories

import com.pasha.auth.internal.data.AuthApi
import com.pasha.auth.internal.domain.models.Credentials
import com.pasha.auth.internal.domain.models.Tokens
import com.pasha.auth.internal.domain.repositories.AuthRepository
import com.pasha.core.network.api.utils.Response
import com.pasha.core.network.api.utils.requestFlow
import com.pasha.core.network.api.models.CredentialsDto
import com.pasha.core.network.api.models.TokensDto
import com.pasha.core.store.api.IdentificationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AuthRepositoryRetrofitImpl @Inject constructor(
    private val authApi: AuthApi,
    private val identificationManager: IdentificationManager
) : AuthRepository {
    override fun signIn(credentials: Credentials): Flow<Response<Tokens>> {
        val credentialsDto = CredentialsDto(
            deviceId = identificationManager.getAndroidId(),
            email = credentials.email,
            password = credentials.password
        )

        return requestFlow {
            authApi.signIn(credentialsDto)
        }.asTokensFlow()
    }

    override fun signUp(credentials: Credentials): Flow<Response<Tokens>> {
        val credentialsDto = CredentialsDto(
            deviceId = identificationManager.getAndroidId(),
            email = credentials.email,
            password = credentials.password
        )

        return requestFlow {
            authApi.signUp(credentialsDto)
        }.asTokensFlow()
    }

    private fun Flow<Response<TokensDto>>.asTokensFlow(): Flow<Response<Tokens>> =
        map { response ->
            when (response) {
                is Response.Success -> Response.Success(
                    Tokens(
                        accessToken = response.data.accessToken,
                        refreshToken = response.data.refreshToken
                    )
                )

                is Response.Loading -> Response.Loading
                is Response.Error -> Response.Error(response.code, response.errorMessage)
            }
        }
}