package com.pasha.all_cards.internal.domain.models

import android.graphics.fonts.FontFamily
import freemarker.ext.beans.StringModel
import java.util.UUID

data class CardData(
    val family: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
)
