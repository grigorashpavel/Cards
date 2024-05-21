package com.pasha.cards.di

import android.app.Activity
import android.app.Application
import android.content.Context
import com.pasha.auth.api.AuthDeps
import com.pasha.cards.CardsApplication
import com.pasha.cards.MainActivity
import com.pasha.core.di.DepsMap
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope
import javax.inject.Singleton


@Scope
annotation class AppScope


@[AppScope Component(modules = [AuthFeatureModule::class])]
interface CardsApplicationComponent : AuthDeps {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): CardsApplicationComponent
    }

    fun inject(application: CardsApplication)
}
