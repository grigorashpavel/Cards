package com.pasha.profile.internal.data

data class ProfileDto(
    val email: String,
    val username: String,
    val headerBackgroundPath: String,
    val avatarPath: String?,
    val password: String?
)
