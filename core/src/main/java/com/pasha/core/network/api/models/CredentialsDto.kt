package com.pasha.core.network.api.models


data class CredentialsDto(
    val deviceId: String,
    val email: String,
    val password: String?
)