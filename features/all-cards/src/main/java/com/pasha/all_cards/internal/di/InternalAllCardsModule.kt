package com.pasha.all_cards.internal.di

import com.pasha.all_cards.internal.data.AllCardsRepositoryRetrofitImpl
import com.pasha.all_cards.internal.data.CardsApi
import com.pasha.all_cards.internal.domain.repositories.AllCardsRepository
import com.pasha.core.di.SessionNetworkProvider
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module
interface InternalAllCardsModule {
    companion object {
        @Provides
        fun provideCardsApi(sessionNetworkProvider: SessionNetworkProvider): CardsApi {
            return sessionNetworkProvider.retrofitBuilder.build().create(CardsApi::class.java)
        }
    }

    @Binds
    fun bindAllCardsRepository(impl: AllCardsRepositoryRetrofitImpl): AllCardsRepository
}