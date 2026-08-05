package com.learn.ai.deen.quran_android.data.repo

import android.content.Context
import android.util.Log
import com.learn.ai.deen.quran_android.data.db.ObjectBox
import com.learn.ai.deen.quran_android.data.model.AyaEntity
import com.learn.ai.deen.quran_android.data.model.AyaModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException
import kotlin.collections.toFloatArray


class QuranJsonRepo constructor(
    private val context: Context
) {
    private val fileRepo = FileRepo()

    suspend fun loadAyasFromJsonAndStore() = withContext(Dispatchers.IO) {
        try {
            val jsonParser = Json { isLenient = true; ignoreUnknownKeys = true }
            repeat(114) { index ->
                val jsonString = fileRepo.readJsonFromAssets(context, "quran_sura_${index + 1}.json")
                if (jsonString != null) {
                    val jsonAyas = jsonParser.decodeFromString<List<AyaModel>>(jsonString)

                    val ayaEntities = jsonAyas.map { jsonAya ->
                        AyaEntity(
                            sura = jsonAya.sura,
                            aya = jsonAya.aya,
                            text = jsonAya.text,
                            md5 = jsonAya.md5,
                            embedding = jsonAya.embedding.map { it.toFloat() }.toFloatArray()
                        )
                    }
                    ObjectBox.ayaBox?.put(ayaEntities)
                    Log.d("QuranJsonRepo", "Successfully loaded sura ${index + 1} with ${ayaEntities.size} Ayas.")
                } else {
                    Log.e("QuranJsonRepo", "JSON file content is null for sura ${index + 1}.")
                }
            }
        } catch (e: IOException) {
            Log.e("QuranJsonRepo", "Error reading JSON file: ${e.message}", e)
        } catch (e: kotlinx.serialization.SerializationException) {
            Log.e("QuranJsonRepo", "Error parsing JSON: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("QuranJsonRepo", "Error loading data from JSON: ${e.message}", e)
        }
    }
}