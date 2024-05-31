package com.pasha.core.account

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import javax.inject.Inject


private const val AUTH_SERVICE_TAG = "AuthenticatorService"

class AuthenticatorService : Service() {
    @Inject
    lateinit var _factory: Authenticator.Factory


    lateinit var _authenticator: Authenticator

    override fun onCreate() {
        super.onCreate()

        DaggerAccountComponent
            .factory()
            .create((applicationContext as AccountDepsProvider).deps)
            .inject(this)

        _authenticator = _factory.create(applicationContext)
        Log.d(AUTH_SERVICE_TAG, "onCreate()")
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(AUTH_SERVICE_TAG, "onBind()")

        return _authenticator.iBinder
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(AUTH_SERVICE_TAG, "onDestroy()")
    }
}