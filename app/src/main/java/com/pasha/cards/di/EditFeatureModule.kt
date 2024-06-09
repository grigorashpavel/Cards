package com.pasha.cards.di

import com.pasha.core.di.DependeciesKey
import com.pasha.core.di.Dependencies
import com.pasha.core.di.SessionNetworkProvider
import com.pasha.edit.api.EditDeps
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
interface EditFeatureModule {
//    @Binds
//    fun bindSessionNetworkProvider(impl: SessionNetworkProviderImpl): SessionNetworkProvider
//    companion object {
//        @Provides
//        fun SessionNetworkProvider(impl: CardsApplicationComponent): SessionNetworkProvider {
//            return impl.sessionNetworkProvider
//        }
//    }

    @Binds
    @IntoMap
    @DependeciesKey(EditDeps::class)
    fun bindDependencies(impl: CardsApplicationComponent): Dependencies
}