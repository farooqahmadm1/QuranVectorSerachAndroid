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

    suspend fun loadAyasFromJsonAndStore(fileName: String) = withContext(Dispatchers.IO) {
        try {
            // 1. Read JSON from assets
            repeat(114) { index ->
                val jsonString = fileRepo.readJsonFromAssets(context,"quran_sura_${index + 1}.json")
                if (jsonString != null) {
                    // 2. Parse JSON
                    //    Configure Json parser to be lenient if your JSON is not strictly formatted
                    val jsonParser = Json { isLenient = true; ignoreUnknownKeys = true }
                    val jsonAyas = jsonParser.decodeFromString<List<AyaModel>>(jsonString)

                    // 3. Map to ObjectBox Entities
                    val ayaEntities = jsonAyas.map { jsonAya ->
                        AyaEntity(
                            sura = jsonAya.sura,
                            aya = jsonAya.aya,
                            text = jsonAya.text,
                            md5 = jsonAya.md5,
                            embedding = jsonAya.embedding.map { it.toFloat() }.toFloatArray()
                        )
                    }
                    // 4. Insert into ObjectBox
                    ObjectBox.ayaBox?.put(ayaEntities)
                    Log.d("****", "Successfully loaded and stored ${ayaEntities.size} Ayas from JSON.")
                    delay(1000)
                } else {
                    Log.e("****", "JSON file content is null.")
                    //_dataState.value = UIState.Error // Or handle appropriately
                }
            }
        } catch (e: IOException) {
            Log.e("****", "Error reading JSON file: ${e.message}", e)
            // _dataState.value = UIState.Error
        } catch (e: kotlinx.serialization.SerializationException) {
            Log.e("****", "Error parsing JSON: ${e.message}", e)
            // _dataState.value = UIState.Error
        } catch (e: Exception) {
            Log.e("****", "Error loading data from JSON: ${e.message}", e)
            // _dataState.value = UIState.Error
        }
    }
}