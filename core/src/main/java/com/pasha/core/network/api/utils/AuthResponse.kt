package com.pasha.core.network.api.utils

sealed class AuthResponse<out T> {
    data object Loading : AuthResponse<Nothing>()
    data class Success<out T>(val data: T) : AuthResponse<T>()
    data class Error(val code: Int, val errorMessage: String) : AuthResponse<Nothing>()
}