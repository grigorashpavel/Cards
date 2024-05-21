package com.pasha.auth.internal.presentation

data class CredentialsState(
    val email: String = "",
    val password: String = "",
    val confirmCode: String = "",
)
