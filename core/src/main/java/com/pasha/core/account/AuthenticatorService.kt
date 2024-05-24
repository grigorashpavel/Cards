package com.pasha.core.account

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log


private const val AUTH_SERVICE_TAG = "AuthenticatorService"

class AuthenticatorService : Service() {
    private lateinit var _authenticator: Authenticator

    override fun onCreate() {
        super.onCreate()

        Log.d(AUTH_SERVICE_TAG, "onCreate()")

        _authenticator = Authenticator(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(AUTH_SERVICE_TAG, "onBind()")

        return  _authenticator.iBinder
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(AUTH_SERVICE_TAG, "onDestroy()")
    }
}