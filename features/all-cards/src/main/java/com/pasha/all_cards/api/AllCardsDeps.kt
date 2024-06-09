package com.pasha.all_cards.api

import com.pasha.core.di.Dependencies
import com.pasha.core.di.SessionNetworkProvider


interface AllCardsDeps: Dependencies {
    val sessionNetworkProvider: SessionNetworkProvider
}