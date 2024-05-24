package com.pasha.cards.di

import android.content.Context
import com.pasha.auth.api.AuthDeps
import com.pasha.auth.api.AuthNavCommandProvider
import com.pasha.auth.api.AuthNetworkProvider
import com.pasha.cards.AuthNavCommandProviderImpl
import com.pasha.cards.AuthNetworkProviderImpl
import com.pasha.core.di.DependeciesKey
import com.pasha.core.di.Dependencies
import com.pasha.core.store.api.IdentificationManager
import com.pasha.core.store.internal.IdentificationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
interface AuthFeatureModule {
    companion object {
        @Provides
        fun provideIdentificationManager(context: Context): IdentificationManager =
            IdentificationManagerImpl(context)

        @Provides
        fun provideAuthNavCommandProvider(): AuthNavCommandProvider = AuthNavCommandProviderImpl()
    }

    @Binds
    fun bindAuthNetworkProvider(impl: AuthNetworkProviderImpl): AuthNetworkProvider

    @Binds
    @IntoMap
    @DependeciesKey(AuthDeps::class)
    fun bindAuthDeps(impl: CardsApplicationComponent): Dependencies
}