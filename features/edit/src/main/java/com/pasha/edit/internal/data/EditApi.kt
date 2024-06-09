package com.pasha.edit.internal.data


import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface EditApi {

    @Multipart
    @POST("upload")
    suspend fun uploadVisitCard(
        @Part("card_name") cardName: RequestBody?,
        @Part card: MultipartBody.Part?
    ): Response<Unit>
}