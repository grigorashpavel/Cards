package com.pasha.auth.internal.di

import com.pasha.auth.api.AuthNetworkProvider
import com.pasha.auth.internal.data.AuthService
import com.pasha.auth.internal.data.repositories.AuthRepositoryRetrofitImpl
import com.pasha.auth.internal.domain.repositories.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
internal interface InternalAuthModule {
    companion object {
        @Provides
        fun providesAuthApi(networkProvider: AuthNetworkProvider): AuthService {
            return networkProvider.retrofitBuilder.build().create(AuthService::class.java)
        }
    }

    @Binds
    fun bindAuthRepository(impl: AuthRepositoryRetrofitImpl): AuthRepository
}