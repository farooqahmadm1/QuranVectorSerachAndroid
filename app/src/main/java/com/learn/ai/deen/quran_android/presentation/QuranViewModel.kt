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

import com.learn.ai.deen.quran_android.data.model.BookmarkEntity
import com.learn.ai.deen.quran_android.data.repo.BookmarkRepo
import com.learn.ai.deen.quran_android.data.repo.QuranTranslationRepo

enum class TranslationMode {
    ALL, ENGLISH, URDU, NONE
}

sealed class UIState {
    data class Success(val data: List<ChapterEntity>) : UIState()
    data object Loading : UIState()
    data object Error : UIState()
}

@HiltViewModel
class QuranViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val translationRepo = QuranTranslationRepo()
    private val bookmarkRepo = BookmarkRepo()

    private val _dataState = MutableStateFlow<UIState>(UIState.Loading)
    val dataState: StateFlow<UIState> = _dataState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<AyaEntity>>(emptyList())
    val searchResults: StateFlow<List<AyaEntity>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _translationMode = MutableStateFlow(TranslationMode.ALL)
    val translationMode: StateFlow<TranslationMode> = _translationMode.asStateFlow()

    private val _selectedTafsirAya = MutableStateFlow<AyaEntity?>(null)
    val selectedTafsirAya: StateFlow<AyaEntity?> = _selectedTafsirAya.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<BookmarkEntity>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkEntity>> = _bookmarks.asStateFlow()

    private val _bookmarkedKeys = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedKeys: StateFlow<Set<String>> = _bookmarkedKeys.asStateFlow()

    private val queryVectorizer by lazy { QueryVectorizer(context) }

    init {
        loadData()
        refreshBookmarks()
    }

    fun refreshBookmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = bookmarkRepo.getAllBookmarks()
            _bookmarks.value = list
            _bookmarkedKeys.value = list.map { "${it.sura}:${it.aya}" }.toSet()
        }
    }

    fun toggleBookmark(sura: Long, aya: Long, note: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            if (bookmarkRepo.isBookmarked(sura, aya)) {
                bookmarkRepo.removeBookmark(sura, aya)
            } else {
                bookmarkRepo.addBookmark(sura, aya, note)
            }
            refreshBookmarks()
        }
    }

    fun updateBookmarkNote(sura: Long, aya: Long, note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepo.addBookmark(sura, aya, note)
            refreshBookmarks()
        }
    }

    fun deleteBookmarkById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepo.deleteBookmarkById(id)
            refreshBookmarks()
        }
    }

    fun setTranslationMode(mode: TranslationMode) {
        _translationMode.value = mode
    }

    fun setTafsirAya(aya: AyaEntity?) {
        _selectedTafsirAya.value = aya
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
            result.map { translationRepo.enrichAya(it) }
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
                        val results = query.find().map { translationRepo.enrichAya(it) }
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