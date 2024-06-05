package com.pasha.profile.internal.domain.repositories

import android.net.Uri
import com.pasha.core.network.api.utils.Response
import com.pasha.profile.internal.domain.repositories.models.Profile
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ProfileRepository {
    fun getProfile(): Flow<Response<Profile>>
    fun updateProfile(avatarUri: Uri?, username: String?): Flow<Response<Unit>>
}