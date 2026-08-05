package com.learn.ai.deen.quran_android.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.ai.deen.quran_android.data.db.ObjectBox
import com.learn.ai.deen.quran_android.data.model.AyaEntity
import com.learn.ai.deen.quran_android.data.model.AyaEntity_
import com.learn.ai.deen.quran_android.data.model.ChapterEntity
import com.learn.ai.deen.quran_android.data.repo.QuranChaptersRepo
import com.learn.ai.deen.quran_android.data.repo.QuranJsonRepo
import com.learn.ai.deen.quran_android.domain.QueryVectorizer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class UIState {
    data class Success(val data: List<ChapterEntity>) : UIState()
    data object Loading : UIState()
    data object Error : UIState()
}

@HiltViewModel
class QuranViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _dataState = MutableStateFlow<UIState>(UIState.Loading)
    val dataState: StateFlow<UIState> = _dataState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<AyaEntity>>(emptyList())
    val searchResults: StateFlow<List<AyaEntity>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val queryVectorizer by lazy { QueryVectorizer(context) }

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _dataState.value = UIState.Loading
            try {
                val chapterBox = ObjectBox.chapterBox
                if (chapterBox == null) {
                    _dataState.value = UIState.Error
                    return@launch
                }

                if (chapterBox.isEmpty) {
                    Log.d("QuranViewModel", "Populating database from JSON assets...")
                    QuranChaptersRepo(context).loadChapterFromJsonAndStore()
                    QuranJsonRepo(context).loadAyasFromJsonAndStore()
                }

                val chapters = chapterBox.all
                if (chapters.isNotEmpty()) {
                    _dataState.value = UIState.Success(chapters)
                } else {
                    Log.e("QuranViewModel", "Chapter box is empty after loading.")
                    _dataState.value = UIState.Error
                }
            } catch (e: Exception) {
                Log.e("QuranViewModel", "Error fetching/populating data", e)
                _dataState.value = UIState.Error
            }
        }
    }

    suspend fun loadSuraById(suraId: Long): List<AyaEntity> = withContext(Dispatchers.IO) {
        try {
            val ayaBox = ObjectBox.ayaBox ?: return@withContext emptyList()
            val query = ayaBox.query(AyaEntity_.sura.equal(suraId)).build()
            val result = query.find()
            query.close()
            result
        } catch (e: Exception) {
            Log.e("QuranViewModel", "Error loading ayas for sura ID: $suraId", e)
            emptyList()
        }
    }

    fun searchAyasByVector(queryText: String) {
        if (queryText.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isSearching.value = true
            try {
                val queryEmbedding = queryVectorizer.getQueryEmbedding(queryText)
                if (queryEmbedding != null) {
                    val box = ObjectBox.ayaBox
                    if (box != null) {
                        val query = box.query(AyaEntity_.embedding.nearestNeighbors(queryEmbedding, 20)).build()
                        val results = query.find()
                        query.close()
                        _searchResults.value = results
                    } else {
                        _searchResults.value = emptyList()
                    }
                } else {
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("QuranViewModel", "Error executing vector search query", e)
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        queryVectorizer.close()
    }
}