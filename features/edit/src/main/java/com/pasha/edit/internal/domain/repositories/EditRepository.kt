package com.pasha.edit.internal.domain.repositories

import com.pasha.core.network.api.utils.Response
import ezvcard.VCard
import kotlinx.coroutines.flow.Flow
import java.sql.Time


interface EditRepository {
    fun uploadCard(cardName: String, vCard: VCard, creationTime: Long): Flow<Response<Unit>>
}