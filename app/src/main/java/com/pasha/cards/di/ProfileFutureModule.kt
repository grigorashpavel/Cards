package com.pasha.cards.di

import android.content.Context
import com.pasha.auth.api.AuthNetworkProvider
import com.pasha.cards.AuthNetworkProviderImpl
import com.pasha.core.account.CardsAccountManager
import com.pasha.core.di.DependeciesKey
import com.pasha.core.di.Dependencies
import com.pasha.core.network.api.NetworkUtil
import com.pasha.core.network.api.SessionService
import com.pasha.core.store.api.IdentificationManager
import com.pasha.core.store.internal.IdentificationManagerImpl
import com.pasha.profile.api.ProfileDeps
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
interface ProfileFutureModule {

    @Binds
    fun bindProfileDepsToAppContext(impl: CardsApplicationComponent): ProfileDeps
    

    @Binds
    @IntoMap
    @DependeciesKey(ProfileDeps::class)
    fun bindProfileDepsToMap(impl: CardsApplicationComponent): Dependencies
}