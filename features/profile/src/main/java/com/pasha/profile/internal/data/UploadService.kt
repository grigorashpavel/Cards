package com.pasha.profile.internal.data

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import com.pasha.core.network.api.utils.Response
import com.pasha.profile.api.ProfileDeps
import com.pasha.profile.api.ProfileDepsProvider
import com.pasha.profile.internal.di.DaggerProfileComponent
import com.pasha.profile.internal.domain.repositories.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val SERVICE_TAG = "UploadService"

class UploadService : Service() {
    @Inject
    lateinit var profileDeps: ProfileDeps

    @Inject
    lateinit var profileRepository: ProfileRepository
    override fun onCreate() {
        super.onCreate()

        Log.d(SERVICE_TAG, "fun onCreate()")

        val provider = applicationContext as ProfileDepsProvider

        DaggerProfileComponent.factory()
            .create(provider.profileDeps, applicationContext)
            .inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(SERVICE_TAG, "fun onStartCommand()")

        val uri = intent?.getStringExtra(IMAGE_URI_KEY)?.let { Uri.parse(it) }
        val username = intent?.getStringExtra(USERNAME_KEY) ?: ""


        CoroutineScope(Dispatchers.IO).launch {
            profileRepository.updateProfile(avatarUri = uri, username = username)
                .onEach { response ->
                    when (response) {
                        is Response.Loading -> {
                            Log.d(SERVICE_TAG, "Response.Loading")
                        }

                        is Response.Error -> {
                            Log.d(SERVICE_TAG, "Response.Error")
                            intent?.putExtra(RESULT_KEY, false)
                            sendBroadcast(intent, null)
                            stopSelf()
                        }

                        is Response.Success -> {
                            Log.d(SERVICE_TAG, "Response.Success")
                            intent?.putExtra(RESULT_KEY, false)
                            sendBroadcast(intent, TRIGGER)
                            stopSelf()
                        }

                    }

                }.collect()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(SERVICE_TAG, "fun onDestroy()")
    }

    companion object {
        internal const val IMAGE_URI_KEY = "image_uri"
        internal const val USERNAME_KEY = "username"

        internal const val TRIGGER = "UploadTrigger"
        internal const val RESULT_KEY = "isSuccess"

    }
}