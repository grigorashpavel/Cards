package com.pasha.core.network.api.utils

import android.util.Log
import com.google.gson.Gson
import com.pasha.core.network.api.models.ErrorResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Call
import retrofit2.HttpException


private const val AUTH_TIMEOUT = 7_000L
private const val REQUEST_FLOW_TAG = "REQUEST_FLOW"


private class NullResponseBodyException(
    message: String = "Expected response body but received null",
    cause: Throwable? = null
) : RuntimeException(message, cause)

fun <T> requestTokensFlow(call: suspend () -> retrofit2.Response<T>): Flow<Response<T>> = flow {
    emit(Response.Loading)

    Log.d(REQUEST_FLOW_TAG, "fun <T> requestTokensFlow() Thread: ${Thread.currentThread().name}")

    withTimeoutOrNull(AUTH_TIMEOUT) {
        val response = call()

        Log.d(
            REQUEST_FLOW_TAG,
            "fun <T> requestTokensFlow().withTimeoutOrNull Thread: ${Thread.currentThread().name}"
        )

        Log.d(REQUEST_FLOW_TAG, "code: ${response.code()}")
        Log.d(REQUEST_FLOW_TAG, "headers: ${response.headers()}")
        Log.d(REQUEST_FLOW_TAG, "message: ${response.message()}")

        try {
            if (response.isSuccessful) {
                Log.d(REQUEST_FLOW_TAG, "Response Success")
                response.body()?.let { tokens ->
                    emit(Response.Success(tokens))
                } ?: throw NullResponseBodyException()
            } else {
                Log.d(REQUEST_FLOW_TAG, "Response Error")
                emit(
                    Response.Error(
                        code = response.code(),
                        errorMessage = response.errorBody()?.string() ?: ""
                    )
                )
            }
        } catch (e: HttpException) {
//            emit(
//                Response.Error(
//                    code = e.code(),
//                    errorMessage = e.message() ?: "Unresolved error in requestTokenFlow"
//                )
//            )
        }
    } ?: emit(Response.Error(code = 408, "Timeout!"))
}.flowOn(Dispatchers.IO)