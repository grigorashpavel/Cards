package com.pasha.core.di

import retrofit2.Retrofit

interface SessionNetworkProvider {
    val retrofitBuilder: Retrofit.Builder
}