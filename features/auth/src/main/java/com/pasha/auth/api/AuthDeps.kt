package com.pasha.auth.api

import com.pasha.core.di.Dependencies
import com.pasha.core.navigation.NavCommand
import com.pasha.core.store.api.IdentificationManager
import retrofit2.Retrofit

interface AuthDeps : Dependencies {
    val authNetworkProvider: AuthNetworkProvider
    val identificationManager: IdentificationManager

    val authNavCommandProvider: AuthNavCommandProvider
}

interface AuthNetworkProvider {
    val retrofitBuilder: Retrofit.Builder
}

interface AuthNavCommandProvider {
    val toAllCards: NavCommand
}