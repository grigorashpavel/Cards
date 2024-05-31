package com.pasha.core.account

import androidx.core.app.AppComponentFactory
import com.pasha.core.di.Dependencies
import com.pasha.core.network.api.SessionService
import com.pasha.core.store.api.IdentificationManager


interface AccountDeps {
    val sessionService: SessionService
    val identificationManager: IdentificationManager
}


interface AccountDepsProvider {
    val deps: AccountDeps
}