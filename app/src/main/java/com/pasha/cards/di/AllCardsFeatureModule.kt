package com.pasha.cards.di

import com.pasha.all_cards.api.AllCardsDeps
import com.pasha.core.di.DependeciesKey
import com.pasha.core.di.Dependencies
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds


@Module
interface AllCardsFeatureModule {
    @Binds
    @IntoMap
    @DependeciesKey(AllCardsDeps::class)
    fun bindDependencies(impl: CardsApplicationComponent): Dependencies
}