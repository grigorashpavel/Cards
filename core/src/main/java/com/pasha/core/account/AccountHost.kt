package com.pasha.core.account

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Bundle

class AccountHost(
    private val accountManager: AccountManager
) {
    fun createAccount() {
        val account = Account("test", "com.pasha.cards")
        val bundle = Bundle().apply {
            putString("access", "access_token_value")
            putString("refresh", "refresh_token_value")
        }

        accountManager.addAccountExplicitly(account, "password", bundle)
    }
}