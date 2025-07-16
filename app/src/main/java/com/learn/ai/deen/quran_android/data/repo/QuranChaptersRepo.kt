package com.learn.ai.deen.quran_android.data.repo

import android.content.Context
import android.util.Log
import com.learn.ai.deen.quran_android.data.db.ObjectBox
import com.learn.ai.deen.quran_android.data.model.ChapterEntity
import com.learn.ai.deen.quran_android.data.model.ChapterModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException


class QuranChaptersRepo(val context: Context) {
    private val fileRepo = FileRepo()

    suspend fun loadChapterFromJsonAndStore() = withContext(Dispatchers.IO) {
        try {
            // 1. Read JSON from assets

            val jsonString = fileRepo.readJsonFromAssets(context, "chapters.json")
            if (jsonString != null) {
                // 2. Parse JSON
                //    Configure Json parser to be lenient if your JSON is not strictly formatted
                val jsonParser = Json { isLenient = true; ignoreUnknownKeys = true }
                val jsonChapters = jsonParser.decodeFromString<List<ChapterModel>>(jsonString)

                // 3. Map to ObjectBox Entities
                val chapterEntities = jsonChapters.map { chapter ->
                    ChapterEntity(
                        sura = chapter.sura,
                        ayasCount = chapter.ayasCount,
                        firstAyaId = chapter.firstAyaId,
                        nameArabic = chapter.nameArabic,
                        nameTransliteration = chapter.nameTransliteration,
                        type = chapter.type,
                        revelationOrder = chapter.revelationOrder,
                        rukus = chapter.rukus
                    )
                }
                // 4. Insert into ObjectBox
                ObjectBox.chapterBox?.put(chapterEntities)
                Log.d("****", "Successfully loaded and stored ${chapterEntities.size} Chapter from JSON.")
            } else {
                Log.e("****", "JSON file content is null.")
                //_dataState.value = UIState.Error // Or handle appropriately
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