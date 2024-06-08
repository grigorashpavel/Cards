package com.pasha.profile.internal.di

import android.content.Context
import com.pasha.core.account.CardsAccountManager
import com.pasha.core.di.SessionNetworkProvider
import com.pasha.core.network.api.QueryAuthenticator
import com.pasha.core.network.api.utils.QueryInterceptor
import com.pasha.core.store.api.FileLoader
import com.pasha.core.store.api.IdentificationManager
import com.pasha.core.store.internal.IdentificationManagerImpl
import com.pasha.profile.internal.data.ProfileApi
import com.pasha.profile.internal.data.repositories.ProfileRepositoryRetrofitImpl
import com.pasha.profile.internal.domain.repositories.ProfileRepository
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient


@Module
interface InternalProfileModule {
    companion object {
        @Provides
        fun providesProfileApi(sessionNetworkProvider: SessionNetworkProvider): ProfileApi {
            return sessionNetworkProvider.retrofitBuilder.build().create(ProfileApi::class.java)
        }

        @Provides
        fun provideFileLoader(context: Context): FileLoader {
            return FileLoader(context)
        }

        @Provides
        fun provideAuthPicasso(interceptor: QueryInterceptor, context: Context): Picasso {
            val okClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            return Picasso.Builder(context)
                .downloader(OkHttp3Downloader(okClient))
                .build()
        }

        @Provides
        fun provideAccountManager(context: Context): CardsAccountManager =
            CardsAccountManager(context)
    }

    @Binds
    fun bindProfileRepository(impl: ProfileRepositoryRetrofitImpl): ProfileRepository
}