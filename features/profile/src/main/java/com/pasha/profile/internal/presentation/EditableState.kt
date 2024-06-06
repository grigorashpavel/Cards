package com.pasha.profile.internal.presentation

import android.net.Uri

internal data class EditableState(
    val imageUri: Uri? = null,
    val username: String? = null
)
