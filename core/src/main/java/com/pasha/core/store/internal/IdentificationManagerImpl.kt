package com.pasha.core.store.internal

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.pasha.core.store.api.IdentificationManager
import javax.inject.Inject

class IdentificationManagerImpl @Inject constructor(
    private val context: Context
) : IdentificationManager {
    @SuppressLint("HardwareIds")
    override fun getAndroidId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}