package com.pasha.auth.internal.presentation

data class ErrorState(
    val emailMessage: String? = null,
    val passwordMessage: String? = null,
    val responseMessage: String? = null,
)
