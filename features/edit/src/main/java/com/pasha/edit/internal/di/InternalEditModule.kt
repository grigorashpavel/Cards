package com.pasha.edit.internal.di

import android.content.Context
import com.pasha.core.cards.CardsManager
import com.pasha.core.di.SessionNetworkProvider
import com.pasha.edit.internal.data.EditApi
import com.pasha.edit.internal.data.repositories.EditRepositoryRetrofitImpl
import com.pasha.edit.internal.domain.repositories.EditRepository
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module
interface InternalEditModule {
    companion object {
        @Provides
        fun provideEditApi(sessionNetworkProvider: SessionNetworkProvider): EditApi =
            sessionNetworkProvider.retrofitBuilder.build().create(EditApi::class.java)

        @Provides
        fun provideCardsManager(context: Context): CardsManager = CardsManager(context)
    }

    @Binds
    fun bindEditRepository(impl: EditRepositoryRetrofitImpl): EditRepository
}