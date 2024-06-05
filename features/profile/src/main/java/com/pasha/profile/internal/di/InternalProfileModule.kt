package com.pasha.profile.internal.di

import android.content.Context
import com.pasha.core.di.SessionNetworkProvider
import com.pasha.core.store.api.FileLoader
import com.pasha.profile.internal.data.ProfileApi
import com.pasha.profile.internal.data.repositories.ProfileRepositoryRetrofitImpl
import com.pasha.profile.internal.domain.repositories.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides


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
    }

    @Binds
    fun bindProfileRepository(impl: ProfileRepositoryRetrofitImpl): ProfileRepository
}