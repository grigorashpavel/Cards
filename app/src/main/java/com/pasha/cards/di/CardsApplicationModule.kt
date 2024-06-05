package com.pasha.cards.di

import android.content.Context
import com.pasha.auth.api.AuthNetworkProvider
import com.pasha.core.account.CardsAccountManager
import com.pasha.core.di.SessionNetworkProvider
import com.pasha.core.network.api.NetworkUtil
import com.pasha.core.network.api.QueryAuthenticator
import com.pasha.core.network.api.SessionService
import com.pasha.core.network.api.utils.QueryInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@Module
interface CardsApplicationModule {
    companion object {
        @Provides
        fun provideAccountManager(context: Context): CardsAccountManager =
            CardsAccountManager(context)

        @Provides
        fun provideSessionService(networkProvider: AuthNetworkProvider): SessionService {
            return networkProvider.retrofitBuilder.build().create(SessionService::class.java)
        }
    }

    @Binds
    fun bindSessionNetworkProvider(impl: SessionNetworkProviderImpl): SessionNetworkProvider
}

@AppScope
class SessionNetworkProviderImpl @Inject constructor(
    authenticator: QueryAuthenticator,
    interceptor: QueryInterceptor
) : SessionNetworkProvider {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .authenticator(authenticator)
        .addInterceptor(interceptor)
        .build()

    override val retrofitBuilder: Retrofit.Builder
        get() = Retrofit.Builder()
            .baseUrl("${NetworkUtil.BASE_ADDRESS}api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)

}