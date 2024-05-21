package com.pasha.auth.internal.data

import com.pasha.core.network.internal.models.CredentialsDto
import com.pasha.core.network.internal.models.TokensDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


internal interface AuthService {
    @POST("auth/login")
    suspend fun signUp(@Body credentials: CredentialsDto): Call<TokensDto>
    @POST("auth/register")
    suspend fun signIn(@Body credentials: CredentialsDto): Call<TokensDto>
}