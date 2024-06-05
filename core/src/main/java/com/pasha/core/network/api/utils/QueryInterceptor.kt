package com.pasha.core.network.api.utils

import android.accounts.AccountManager
import com.pasha.core.account.CardsAccountManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class QueryInterceptor @Inject constructor(
    private val accountManager: CardsAccountManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val origRequest = chain.request()

        val accessToken = accountManager.getAuthTokenSync(
            accountManager.activeAccount,
            CardsAccountManager.ACCESS_TOKEN
        )

        val newRequest = origRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(newRequest)
    }
}