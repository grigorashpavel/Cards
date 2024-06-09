package com.pasha.edit.internal.presentation

data class EditState(
    val isUploaded: Boolean = false,
    val isLoading: Boolean = false,
    val errorCode: Int? = null,
    val errorMessage: String? = null
)