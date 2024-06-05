package com.pasha.core.store.api

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class FileLoader @Inject constructor(
    private val context: Context
) {
    suspend fun loadFile(uri: Uri?): File? = withContext(Dispatchers.IO) {
        if (uri == null) return@withContext null

        val file = File(context.cacheDir, "temp_file")
        val inputStream = context.contentResolver.openInputStream(uri)
        try {
            inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    input?.copyTo(output)
                }

                return@withContext file
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext null
    }
}
