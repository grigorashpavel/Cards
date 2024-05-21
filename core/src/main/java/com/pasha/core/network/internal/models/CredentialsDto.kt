package com.pasha.core.network.internal.models


data class CredentialsDto(
    val deviceId: String,
    val email: String,
    val password: String?
)