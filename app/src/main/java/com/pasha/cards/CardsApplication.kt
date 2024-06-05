package com.pasha.cards

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavOptions
import com.pasha.auth.api.AuthNavCommandProvider
import com.pasha.auth.api.AuthNetworkProvider
import com.pasha.cards.di.AppScope
import com.pasha.cards.di.DaggerCardsApplicationComponent
import com.pasha.core.account.AccountDeps
import com.pasha.core.account.AccountDepsProvider
import com.pasha.core.di.DepsMap
import com.pasha.core.di.HasDependencies
import com.pasha.core.navigation.NavCommand
import com.pasha.core.network.api.NetworkUtil
import com.pasha.profile.api.PreferencesManager
import com.pasha.profile.api.ProfileDeps
import com.pasha.profile.api.ProfileDepsProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import javax.inject.Inject

class CardsApplication : Application(), HasDependencies, AccountDepsProvider, ProfileDepsProvider {
    @Inject
    override lateinit var depsMap: DepsMap

    @Inject
    override lateinit var deps: AccountDeps

    @Inject
    override lateinit var profileDeps: ProfileDeps

    override fun onCreate() {
        super.onCreate()

        val preferences = PreferencesManager(applicationContext)

        AppCompatDelegate.setDefaultNightMode(preferences.themeCode)

        DaggerCardsApplicationComponent.factory()
            .create(this)
            .inject(this)
    }
}

@AppScope
class AuthNetworkProviderImpl @Inject constructor() : AuthNetworkProvider {
    override val retrofitBuilder: Retrofit.Builder
        get() = Retrofit.Builder()
            .baseUrl(NetworkUtil.BASE_ADDRESS)
            .addConverterFactory(GsonConverterFactory.create())
}

class AuthNavCommandProviderImpl @Inject constructor() : AuthNavCommandProvider {
    private val navOptions = NavOptions.Builder()
        .setPopUpTo(R.id.all_cards, true)
        .build()

    override val toAllCards: NavCommand =
        NavCommand(R.id.all_cards, navOptions = navOptions)
}