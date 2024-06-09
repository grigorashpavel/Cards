package com.pasha.all_cards.internal.presentation

import androidx.core.app.NotificationCompat.MessagingStyle.Message


data class AllCardsState(
    val isLoading: Boolean = false,
    val errorCode: Int? = null,
    val errorMessage: String? = null
)