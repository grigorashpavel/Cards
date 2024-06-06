package com.pasha.profile.api

import com.pasha.core.di.Dependencies
import com.pasha.core.di.SessionNetworkProvider
import com.pasha.core.network.api.QueryAuthenticator
import com.pasha.core.network.api.SessionService

interface ProfileDeps : Dependencies {
    val sessionNetworkProvider: SessionNetworkProvider
    val sessionService: SessionService
    val authenticator: QueryAuthenticator
}

interface ProfileDepsProvider {
    val profileDeps: ProfileDeps
}