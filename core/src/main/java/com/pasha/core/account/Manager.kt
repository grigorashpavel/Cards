package com.pasha.core.account

import android.accounts.AccountManager
import android.content.Context

class Manager(context: Context) {
    val manager = AccountManager.get(context)

    fun test() {

    }
}