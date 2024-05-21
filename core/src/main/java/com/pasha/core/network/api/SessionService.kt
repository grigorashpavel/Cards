package com.pasha.core.network.api


interface SessionService {
    suspend fun stopCurrentSession()
    suspend fun stopOtherSessions()
    suspend fun extendSession()
}