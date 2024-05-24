package com.pasha.auth.internal.domain.repositories

import com.pasha.auth.internal.domain.models.Credentials
import com.pasha.auth.internal.domain.models.Tokens
import com.pasha.core.network.api.utils.Response
import kotlinx.coroutines.flow.Flow


internal interface AuthRepository {
    fun signIn(credentials: Credentials): Flow<Response<Tokens>>
    fun signUp(credentials: Credentials): Flow<Response<Tokens>>
}