package com.pasha.cards.di

import com.pasha.core.di.DependeciesKey
import com.pasha.core.di.Dependencies
import com.pasha.profile.api.ProfileDeps
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
interface ProfileFutureModule {
    @Binds
    fun bindProfileDepsToAppContext(impl: CardsApplicationComponent): ProfileDeps

    @Binds
    @IntoMap
    @DependeciesKey(ProfileDeps::class)
    fun bindProfileDepsToMap(impl: CardsApplicationComponent): Dependencies
}