package com.pasha.cards.di

import com.pasha.auth.api.AuthNetworkProvider
import com.pasha.core.account.AccountDeps
import com.pasha.core.network.api.SessionService
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module
interface AccountAuthModule {
    companion object {
        @Provides
        fun provideSessionService(networkProvider: AuthNetworkProvider): SessionService {
            return networkProvider.retrofitBuilder.build().create(SessionService::class.java)
        }
    }

    @Binds
    fun bindAccountDeps(impl: CardsApplicationComponent): AccountDeps
}