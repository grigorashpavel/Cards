package com.pasha.all_cards.internal.data

import com.pasha.all_cards.internal.data.models.AllCardsWrapper
import com.pasha.all_cards.internal.data.models.CardDto
import com.pasha.all_cards.internal.domain.repositories.AllCardsRepository
import com.pasha.all_cards.internal.domain.repositories.CardsResponse
import com.pasha.all_cards.internal.domain.models.Card
import com.pasha.core.network.api.utils.Response
import com.pasha.core.network.api.utils.requestFlow
import ezvcard.Ezvcard
import ezvcard.VCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.util.UUID
import javax.inject.Inject

class AllCardsRepositoryRetrofitImpl @Inject constructor(
    private val cardsApi: CardsApi
) : AllCardsRepository {
    override fun getAllCards(): Flow<Response<List<Card>>> {
        return requestFlow {
            cardsApi.getAllCards()
        }.asCardsFlow()
    }

    override fun getCardById(id: UUID): Flow<CardsResponse<VCard>> {
        return requestFlow {
            cardsApi.getCardById(id)
        }.asVCardFlow()
    }

    override fun getCardsByName(name: String): Flow<Response<List<Card>>> {
        return requestFlow {
            cardsApi.getCardsByName(name)
        }.asCardsFlow()
    }

    override fun editCardById(id: UUID, cardName: String, card: VCard): Flow<CardsResponse<Unit>> {
        TODO("Not yet implemented")
    }

    override fun deleteCardById(id: UUID): Flow<CardsResponse<Unit>> {
        return requestFlow {
            cardsApi.deleteCard(id)
        }
    }

    private fun Flow<Response<AllCardsWrapper>>.asCardsFlow(): Flow<Response<List<Card>>> =
        map { response ->
            when (response) {
                is Response.Loading -> Response.Loading
                is Response.Error -> Response.Error(response.code, response.errorMessage)
                is Response.Success -> Response.Success(response.data.cards.map { it.toCard() })
            }
        }

    private fun CardDto.toCard(): Card = Card(
        cardName = this.cardName,
        cardId = UUID.fromString(this.cardId),
        creationTime = this.creationTime
    )

    private fun Flow<Response<ResponseBody>>.asVCardFlow(): Flow<Response<VCard>> {
        return map { response ->
            when (response) {
                is Response.Loading -> Response.Loading
                is Response.Error -> Response.Error(response.code, response.errorMessage)
                is Response.Success -> Response.Success(response.data.toVCard())
            }
        }
    }

    private fun ResponseBody.toVCard(): VCard {
        val vcard = Ezvcard.parse(this.byteStream()).first()
        return vcard
    }
}