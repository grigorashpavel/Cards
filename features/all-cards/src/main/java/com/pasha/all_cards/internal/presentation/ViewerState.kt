package com.pasha.all_cards.internal.presentation

data class ViewerState(
    val isLoading: Boolean = false,
    val errorCode: Int? = null,
    val errorMessage: String? = null
)