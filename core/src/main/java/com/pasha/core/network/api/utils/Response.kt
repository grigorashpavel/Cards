package com.pasha.core.network.api.utils

sealed class Response<out T> {
    data object Loading : Response<Nothing>()
    data class Success<out T>(val data: T) : Response<T>()
    data class Error(val code: Int, val errorMessage: String) : Response<Nothing>()
}