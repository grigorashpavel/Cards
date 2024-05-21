package com.pasha.core.network.api.utils

import com.google.gson.Gson
import com.pasha.core.network.internal.models.ErrorResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Call


private const val AUTH_TIMEOUT = 15_000L

fun <T> requestTokensFlow(call: suspend () -> Call<T>): Flow<AuthResponse<T>> = flow {
    emit(AuthResponse.Loading)

    withTimeoutOrNull(AUTH_TIMEOUT) {
        val response = call().execute()

        try {
            if (response.isSuccessful) {
                response.body()?.let { tokens ->
                    emit(AuthResponse.Success(tokens))
                }
            } else {
                response.errorBody()?.let { error ->
                    error.close()
                    val parsedError =
                        Gson().fromJson(error.charStream(), ErrorResponseDto::class.java)

                    emit(
                        AuthResponse.Error(
                            code = parsedError.code,
                            errorMessage = parsedError.message
                                ?: throw IllegalStateException("Unexpected Empty Body when auth")
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(AuthResponse.Error(code = 400, errorMessage = e.message ?: ""))
        }
    } ?: emit(AuthResponse.Error(code = 408, "Timeout!"))
}.flowOn(Dispatchers.IO)