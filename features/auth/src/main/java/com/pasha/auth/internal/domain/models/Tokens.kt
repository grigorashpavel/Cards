package com.pasha.auth.internal.domain.models

internal data class Tokens(
    val accessToken: String,
    val refreshToken: String
)
