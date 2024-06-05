package com.pasha.profile.internal.data.repositories

import android.net.Uri
import com.pasha.core.network.api.SessionService
import com.pasha.core.network.api.utils.Response
import com.pasha.core.network.api.utils.requestFlow
import com.pasha.core.store.api.FileLoader
import com.pasha.profile.internal.data.ProfileApi
import com.pasha.profile.internal.data.ProfileDto
import com.pasha.profile.internal.domain.repositories.ProfileRepository
import com.pasha.profile.internal.domain.repositories.models.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ProfileRepositoryRetrofitImpl @Inject constructor(
    private val sessionService: SessionService,
    private val profileApi: ProfileApi,
    private val fileLoader: FileLoader
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

            if (file?.exists() == true) file.delete()

            profileApi.updateProfile(usernameRequestBody, avatarParts)
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
}