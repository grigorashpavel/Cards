package com.pasha.core.account

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.pasha.core.network.api.SessionService
import com.pasha.core.network.api.models.CredentialsDto
import com.pasha.core.network.api.utils.Response
import com.pasha.core.network.api.utils.requestTokensFlow
import com.pasha.core.store.api.IdentificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking


private const val AUTH_TAG = "Authenticator"

class Authenticator @AssistedInject constructor(
    @Assisted("context") private val context: Context,
    private val sessionService: SessionService,
    private val identificationManager: IdentificationManager
) : AbstractAccountAuthenticator(context) {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("context") context: Context): Authenticator
    }

    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle? = null

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        Log.d(AUTH_TAG, "addAccount()")

        val intentFromSettings = Intent("com.pasha.cards.ADD_ACCOUNT")

        return Bundle().apply {
            putParcelable(AccountManager.KEY_INTENT, intentFromSettings)
        }
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle? = null

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        Log.d(AUTH_TAG, "Authenticator: fun getAuthToken()")

        val manager = CardsAccountManager(context)

        var errorCode = ""
        var errorMessage = ""

        var accessToken: String? = null

        val refreshToken = manager.getAuthTokenSync(account, KEY_REFRESH_TOKEN)
        Log.d(AUTH_TAG, "Authenticator refreshToken: $refreshToken")
        if (refreshToken != null) {
            val credentialsDto = CredentialsDto(
                email = account!!.name,
                password = "",
                deviceId = identificationManager.getAndroidId()
            )

            Log.d(AUTH_TAG, "Authenticator try to block thread for async call")
            runBlocking {
                Log.d(AUTH_TAG, "Authenticator try to call requestTokensFLow")
                requestTokensFlow {
                    sessionService.extendSession("Bearer $refreshToken", credentialsDto)
                }.onEach { response ->
                    when (response) {
                        is Response.Success -> {
                            Log.d(AUTH_TAG, "Authenticator: isSuccess into requestTokensFlow")
                            manager.invalidateTokens(refreshToken)
                            manager.setTokensOnActiveAccount(
                                response.data.accessToken,
                                response.data.refreshToken
                            )

                            accessToken = response.data.accessToken
                        }

                        is Response.Loading -> {
                            Log.d(AUTH_TAG, "Authenticator: isLoading into requestTokensFlow")
                        }

                        is Response.Error -> {
                            Log.d(AUTH_TAG, "Authenticator: isError into requestTokensFlow")

                            errorCode = response.code.toString()
                            errorMessage = response.errorMessage

                            if (response.code == 401) {
                                manager.invalidateTokens(refreshToken)
                            }
                        }
                    }
                }.collect()

                Log.d(AUTH_TAG, "Authenticator leave from runBlocking")
            }
        }


        if (accessToken != null) {
            Log.d(AUTH_TAG, "Authenticator return accessToken: $accessToken")
            val result = Bundle()
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account?.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account?.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, accessToken)

            return result
        }

        Log.d(AUTH_TAG, "Authenticator return error: $errorMessage")
        return Bundle().apply {
            putString(AccountManager.KEY_ERROR_CODE, errorCode)
            putString(AccountManager.KEY_ERROR_MESSAGE, errorMessage)
        }
    }

    override fun getAuthTokenLabel(authTokenType: String?): String? = null

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle? = null

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle? = null

    companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}