package com.pasha.edit.api

import com.pasha.core.di.Dependencies
import com.pasha.core.di.SessionNetworkProvider


interface EditDeps : Dependencies {
    val sessionNetworkProvider: SessionNetworkProvider
}