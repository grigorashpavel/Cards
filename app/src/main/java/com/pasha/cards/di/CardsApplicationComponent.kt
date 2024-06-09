package com.pasha.cards.di

import android.app.Activity
import android.app.Application
import android.content.Context
import com.pasha.auth.api.AuthDeps
import com.pasha.auth.api.AuthNetworkProvider
import com.pasha.cards.CardsApplication
import com.pasha.cards.MainActivity
import com.pasha.core.account.AccountDeps
import com.pasha.core.di.DependeciesKey
import com.pasha.core.di.Dependencies
import com.pasha.core.di.DepsMap
import com.pasha.core.network.api.SessionService
import com.pasha.edit.api.EditDeps
import com.pasha.profile.api.ProfileDeps
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.create
import javax.inject.Scope
import javax.inject.Singleton


@Scope
annotation class AppScope


@[AppScope Component(
    modules = [
        AuthFeatureModule::class,
        AccountAuthModule::class,
        CardsApplicationModule::class,
        ProfileFutureModule::class,
        EditFeatureModule::class
    ]
)]
interface CardsApplicationComponent : AuthDeps, AccountDeps, ProfileDeps, EditDeps {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): CardsApplicationComponent
    }

    fun inject(application: CardsApplication)
}
