package com.pasha.all_cards.internal.domain.models

import java.util.UUID

data class Card(
    val cardName: String,
    val creationTime: String,
    val cardId: UUID
)
