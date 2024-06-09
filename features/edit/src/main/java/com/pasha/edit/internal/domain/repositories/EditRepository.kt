package com.pasha.edit.internal.domain.repositories

import com.pasha.core.network.api.utils.Response
import ezvcard.VCard
import kotlinx.coroutines.flow.Flow


interface EditRepository {
    fun uploadCard(cardName: String, vCard: VCard): Flow<Response<Unit>>
}