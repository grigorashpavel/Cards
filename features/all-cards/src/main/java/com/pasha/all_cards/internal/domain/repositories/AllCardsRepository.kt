package com.pasha.all_cards.internal.domain.repositories

import com.pasha.all_cards.internal.data.models.CardDto
import com.pasha.all_cards.internal.domain.models.Card
import ezvcard.VCard
import kotlinx.coroutines.flow.Flow
import java.util.UUID

internal typealias CardsResponse<T> = com.pasha.core.network.api.utils.Response<T>

interface AllCardsRepository {
    fun getAllCards(): Flow<CardsResponse<List<Card>>>
    fun getCardById(id: UUID): Flow<CardsResponse<VCard>>
    fun getCardsByName(name: String): Flow<CardsResponse<List<Card>>>
    fun editCardById(id: UUID, cardName: String, card: VCard): Flow<CardsResponse<Unit>>
    fun deleteCardById(id: UUID): Flow<CardsResponse<Unit>>
}