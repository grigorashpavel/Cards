package com.pasha.core.network.api

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import com.pasha.core.account.CardsAccountManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import kotlin.math.truncate

class QueryAuthenticator @Inject constructor(
    private val accountManager: CardsAccountManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val expiredAccessToken = accountManager.getAuthTokenSync(
            accountManager.activeAccount,
            CardsAccountManager.ACCESS_TOKEN
        )
        accountManager.invalidateTokens(expiredAccessToken)

        val accessToken = runBlocking {
            accountManager.getAuthTokenAsync(accountManager.activeAccount!!)
        }
        if (accessToken == null) return null

        val newRequest = response.request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        return newRequest
    }
}