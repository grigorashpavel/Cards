package com.pasha.core.navigation

import android.os.Bundle
import androidx.navigation.NavOptions
import okio.Options

data class NavCommand(
    val action: Int,
    val args: Bundle? = null,
    val navOptions: NavOptions? = null
)
