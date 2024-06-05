package com.pasha.profile.internal.presentation

import java.lang.Error

data class ProfileState(
    val email: String = "",
    val username: String ="",
    val headerPath: String = "",
    val avatarPath: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
