package com.pasha.all_cards.internal.data

import com.pasha.all_cards.internal.data.models.AllCardsWrapper
import com.pasha.all_cards.internal.data.models.CardDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import java.util.UUID

interface CardsApi {
    @GET("cards")
    suspend fun getAllCards(): Response<AllCardsWrapper>

    @GET("cards/by-id/{id}")
    suspend fun getCardById(
        @Path("id") cardId: UUID
    ): Response<ResponseBody>

    @GET("cards/{name}")
    suspend fun getCardsByName(
        @Path("name") cardName: String
    ): Response<AllCardsWrapper>

    @Multipart
    @PUT("cards/{id}")
    suspend fun editCard(
        @Path("id") cardId: UUID,
        @Part("cardName") cardName: RequestBody,
        @Part cardFile: MultipartBody.Part,
    ): Response<Unit>

    @DELETE("cards/{id}")
    suspend fun deleteCard(
        @Path("id") cardId: UUID,
    ): Response<Unit>
}