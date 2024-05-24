package com.pasha.core.account


import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardsAccount(
    val email: String,
    val password: String,
    val accessToken: String,
    val refreshToken: String
) : Parcelable