package com.pasha.core.store.api

interface IdentificationManager {
    fun getAndroidId(): String
    fun getDeviceName(): String
}