package com.pasha.cards.di

import androidx.navigation.NavOptions
import com.pasha.auth.api.AuthNavCommandProvider
import com.pasha.auth.api.AuthNetworkProvider
import com.pasha.cards.AuthNavCommandProviderImpl
import com.pasha.cards.R
import com.pasha.core.account.AccountDeps
import com.pasha.core.navigation.NavCommand
import com.pasha.core.network.api.SessionService
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Inject


@Module
interface AccountAuthModule {
    @Binds
    fun bindAccountDeps(impl: CardsApplicationComponent): AccountDeps
}