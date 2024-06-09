package com.pasha.edit.internal.data.repositories

import android.annotation.SuppressLint
import com.pasha.core.cards.CardsManager
import com.pasha.core.network.api.utils.Response
import com.pasha.core.network.api.utils.requestFlow
import com.pasha.edit.internal.data.EditApi
import com.pasha.edit.internal.domain.repositories.EditRepository
import ezvcard.VCard
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class EditRepositoryRetrofitImpl @Inject constructor(
    private val editApi: EditApi,
    private val cardsManager: CardsManager
) : EditRepository {
    override fun uploadCard(
        cardName: String,
        vCard: VCard,
        creationTime: Long
    ): Flow<Response<Unit>> {
        return requestFlow {
            val cardNameRequestBody = cardName.toRequestBody("text/plain".toMediaType())

            val file = cardsManager.cardToFile(vCard, cardName)
            val fileBody = file?.asRequestBody("text/vcard".toMediaType())
            val cardParts = fileBody?.let { body ->
                MultipartBody.Part.createFormData("card", file.name, body)
            }
            val time = formatTime(creationTime).toRequestBody("text/plain".toMediaType())

            editApi.uploadVisitCard(
                cardName = cardNameRequestBody,
                card = cardParts,
                creationTime = time
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun formatTime(mills: Long): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        return dateFormat.format(Date(mills))
    }
}