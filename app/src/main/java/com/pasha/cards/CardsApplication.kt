package com.pasha.cards

import android.app.Application
import com.pasha.auth.api.AuthNavCommandProvider
import com.pasha.auth.api.AuthNetworkProvider
import com.pasha.cards.di.AppScope
import com.pasha.cards.di.DaggerCardsApplicationComponent
import com.pasha.core.di.DepsMap
import com.pasha.core.di.HasDependencies
import com.pasha.core.navigation.NavCommand
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class CardsApplication : Application(), HasDependencies {
    @Inject
    override lateinit var depsMap: DepsMap

    override fun onCreate() {
        super.onCreate()

        DaggerCardsApplicationComponent.factory()
            .create(this)
            .inject(this)
    }
}

@AppScope
class AuthNetworkProviderImpl @Inject constructor() : AuthNetworkProvider {
    override val retrofitBuilder: Retrofit.Builder
        get() = Retrofit.Builder()
            .baseUrl("http://192.168.0.107:8080/")
            .addConverterFactory(GsonConverterFactory.create())
}

class AuthNavCommandProviderImpl @Inject constructor() : AuthNavCommandProvider {
    override val toAllCards: NavCommand =
        NavCommand(R.id.action_global_all_cards_feature_nested_graph)
}