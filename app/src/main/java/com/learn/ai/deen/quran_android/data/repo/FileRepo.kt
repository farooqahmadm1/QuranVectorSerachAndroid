package com.learn.ai.deen.quran_android.data.repo

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class FileRepo {
    /**
     * Reads a JSON file from the assets folder and returns its content as a String.
     *
     * @param fileName The name of the JSON file to read.
     * @return The content of the JSON file as a String, or null if an error occurs.
     */
    suspend fun readJsonFromAssets(context: Context, fileName: String): String? = withContext(Dispatchers.IO) {
        try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            Log.e("FileRepo", "Error reading asset file: $fileName", ioException)
            null
        } catch (outOfMemoryError: OutOfMemoryError) {
            Log.e("FileRepo", "Out of memory while reading asset file: $fileName", outOfMemoryError)
            null
        } catch (e: Exception) {
            Log.e("FileRepo", "Unexpected error reading asset file: $fileName", e)
            null
        }
    }
}