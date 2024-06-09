package com.pasha.core.cards

import android.content.Context
import ezvcard.VCard
import ezvcard.VCardVersion
import ezvcard.io.text.VCardWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.StringWriter
import javax.inject.Inject
import kotlin.contracts.contract

class CardsManager(private val context: Context) {
    companion object {
        suspend fun cardToString(vCard: VCard): String? {
            return try {
                val sw = StringWriter()
                VCardWriter(sw, VCardVersion.V3_0).use { writer ->
                    writer.write(vCard)
                }
                val vcardString = sw.toString()
                vcardString
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }


    suspend fun cardToFile(vCard: VCard, cardName: String): File? {
        return try {
            val file = File(context.cacheDir, "$cardName.vcf")
            FileOutputStream(file).use { stream ->
                VCardWriter(stream, VCardVersion.V3_0).use { writer ->
                    writer.write(vCard)
                }
            }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}