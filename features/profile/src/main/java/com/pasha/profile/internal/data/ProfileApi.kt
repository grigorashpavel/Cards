package com.pasha.profile.internal.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileApi {
    @GET("profile")
    suspend fun getProfile(): Response<ProfileDto>

    @Multipart
    @POST("profile")
    suspend fun updateProfile(
        @Part("username") username: RequestBody?,
        @Part avatar: MultipartBody.Part?
    ): Response<Unit>
}