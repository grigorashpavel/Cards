package com.pasha.auth.internal.presentation

import android.provider.ContactsContract.Data

data class CredentialsState(
    val email: String = "",
    val password: String = "",
    val confirmCode: String = "",
)
