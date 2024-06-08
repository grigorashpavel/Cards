package com.pasha.profile.api

import com.pasha.core.di.Dependencies
import com.pasha.core.di.SessionNetworkProvider
import com.pasha.core.network.api.QueryAuthenticator
import com.pasha.core.network.api.SessionService
import com.pasha.core.network.api.utils.QueryInterceptor
import com.pasha.core.store.api.IdentificationManager

interface ProfileDeps : Dependencies {
    val sessionNetworkProvider: SessionNetworkProvider
    val sessionService: SessionService
    val interceptor: QueryInterceptor
    val identificationManager: IdentificationManager
}

interface ProfileDepsProvider {
    val profileDeps: ProfileDeps
}