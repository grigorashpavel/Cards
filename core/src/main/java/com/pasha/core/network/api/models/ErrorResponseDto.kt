package com.pasha.core.network.api.models

import com.google.gson.annotations.SerializedName

data class ErrorResponseDto(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String?
)
