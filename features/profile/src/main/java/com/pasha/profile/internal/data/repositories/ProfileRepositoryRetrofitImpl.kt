package com.pasha.profile.internal.data.repositories

import android.net.Uri
import android.util.Log
import com.pasha.core.account.CardsAccountManager
import com.pasha.core.network.api.SessionService
import com.pasha.core.network.api.models.CredentialsDto
import com.pasha.core.network.api.models.DevicesListWrapper
import com.pasha.core.network.api.utils.Response
import com.pasha.core.network.api.utils.requestFlow
import com.pasha.core.store.api.FileLoader
import com.pasha.core.store.api.IdentificationManager
import com.pasha.profile.internal.data.ProfileApi
import com.pasha.profile.internal.data.ProfileDto
import com.pasha.profile.internal.domain.repositories.ProfileRepository
import com.pasha.profile.internal.domain.repositories.models.Device
import com.pasha.profile.internal.domain.repositories.models.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ProfileRepositoryRetrofitImpl @Inject constructor(
    private val sessionService: SessionService,
    private val profileApi: ProfileApi,
    private val fileLoader: FileLoader,
    private val identificationManager: IdentificationManager,
    private val accountManager: CardsAccountManager
) : ProfileRepository {
    override fun getProfile(): Flow<Response<Profile>> {
        return requestFlow(profileApi::getProfile).asProfileFlow()
    }

    override fun updateProfile(avatarUri: Uri?, username: String?): Flow<Response<Unit>> {
        return requestFlow {
            val file = fileLoader.loadFile(avatarUri)
            val usernameRequestBody = username?.toRequestBody("text/plain".toMediaTypeOrNull())

            val avatarRequestBody = file?.asRequestBody("image/*".toMediaTypeOrNull())
            val avatarParts = avatarRequestBody?.let { body ->
                MultipartBody.Part.createFormData("avatar", file.name, body)
            }

            profileApi.updateProfile(usernameRequestBody, avatarParts)
        }
    }

    override fun getActiveSessions(): Flow<Response<List<Device>>> {
        val token = accountManager.getAuthTokenSync(
            accountManager.activeAccount,
            CardsAccountManager.ACCESS_TOKEN
        )

        return requestFlow {
            sessionService.getActiveSessions("Bearer $token")
        }.asDeviceFlow()
    }

    override suspend fun killCurrentSession() {
        val email = accountManager.activeAccount?.name
        if (email != null) {
            val deviceId = identificationManager.getAndroidId()
            val credentials =
                CredentialsDto(deviceName = "", password = "", deviceId = deviceId, email = email!!)

            requestFlow {
                sessionService.stopCurrentSession(credentials)
            }.collect()
            accountManager.exitFromAccount()
        } else {
            Log.d("PROFILE_REP_IMPL", "Error, email null on exist current!")
        }
    }

    override suspend fun killOtherSessions() {
        val email = accountManager.activeAccount?.name
        if (email != null) {
            val deviceId = identificationManager.getAndroidId()
            val credentials =
                CredentialsDto(deviceName = "", password = "", deviceId = deviceId, email = email!!)

            requestFlow {
                sessionService.stopOtherSessions(credentials)
            }.collect()
        } else {
            Log.d("PROFILE_REP_IMPL", "Error, email null on exist other!")
        }
    }


    private fun Flow<Response<ProfileDto>>.asProfileFlow(): Flow<Response<Profile>> =
        map { response ->
            when (response) {
                is Response.Success -> Response.Success(
                    Profile(
                        email = response.data.email,
                        username = response.data.username,
                        avatarPath = response.data.avatarPath ?: "",
                        headerPath = response.data.headerBackgroundPath,
                    )
                )

                is Response.Loading -> Response.Loading
                is Response.Error -> Response.Error(response.code, response.errorMessage)
            }
        }

    private fun Flow<Response<DevicesListWrapper>>.asDeviceFlow(): Flow<Response<List<Device>>> =
        map { response ->
            when (response) {
                is Response.Success -> Response.Success(
                    response.data.activeSessions.map { dto -> Device(dto.deviceId, dto.deviceName) }
                )

                is Response.Loading -> Response.Loading
                is Response.Error -> Response.Error(response.code, response.errorMessage)
            }
        }
}