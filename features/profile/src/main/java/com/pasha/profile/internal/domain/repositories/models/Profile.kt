package com.pasha.profile.internal.domain.repositories.models

data class Profile(
    val email: String = "",
    val username: String = "",
    val avatarPath: String = "",
    val headerPath: String = ""
)
