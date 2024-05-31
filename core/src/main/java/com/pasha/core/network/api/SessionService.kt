package com.pasha.core.network.api

import com.pasha.core.network.api.models.CredentialsDto
import com.pasha.core.network.api.models.TokensDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface SessionService {
    @POST("sessions/stop-current")
    suspend fun stopCurrentSession(@Body credentialsDto: CredentialsDto)

    @POST("sessions/stop-other")
    suspend fun stopOtherSessions(@Body credentialsDto: CredentialsDto)

    @POST("sessions/extend-current")
    suspend fun extendSession(
        @Header("Authorization") refreshToken: String, @Body credentialsDto: CredentialsDto
    ): Response<TokensDto>

    @POST
    suspend fun validateToken(token: String): Response<Unit>
}