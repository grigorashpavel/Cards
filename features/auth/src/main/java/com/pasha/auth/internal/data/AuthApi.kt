package com.pasha.auth.internal.data

import com.pasha.core.network.api.models.CredentialsDto
import com.pasha.core.network.api.models.TokensDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


internal interface AuthApi {
    @POST("auth/register")
    suspend fun signUp(@Body credentials: CredentialsDto): Response<TokensDto>

    @POST("auth/login")
    suspend fun signIn(@Body credentials: CredentialsDto): Response<TokensDto>
}