package com.learn.ai.deen.quran_android.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.ai.deen.quran_android.QuranVectorSearchApplication
import com.learn.ai.deen.quran_android.data.db.ObjectBox
import com.learn.ai.deen.quran_android.data.model.AyaEntity
import com.learn.ai.deen.quran_android.data.model.AyaEntity_
import com.learn.ai.deen.quran_android.data.model.ChapterEntity
import com.learn.ai.deen.quran_android.data.repo.QuranChaptersRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class UIState {
    data class Success(val data: List<ChapterEntity>) : UIState()
    data object Loading : UIState()
    data object Error : UIState()
}

@HiltViewModel
class QuranViewModel @Inject constructor() : ViewModel() {
    private var _dataState = MutableStateFlow<UIState>(UIState.Loading)
    val dataState: StateFlow<UIState> = _dataState.onStart {
        try {

        } catch (e: Exception) {
            Log.e("****", "Error initializing data", e)
            _dataState.value = UIState.Error
        }

    }.stateIn(viewModelScope, started = WhileSubscribed(5000), initialValue = UIState.Loading)

    init {
        viewModelScope.launch {
            try {
                ObjectBox.chapterBox?.all?.let { ayaEntity ->
                    _dataState.value = UIState.Success(ayaEntity)
                } ?: run {
                    Log.d("*****", "failed to get ayaEntity")
                    _dataState.value = UIState.Error
                }
            } catch (e: Exception) {
                Log.e("****", "Error fetching data", e)
                _dataState.value = UIState.Error
            }
        }
    }


    suspend fun loadSuraById(suraId: Long): List<AyaEntity> = withContext(Dispatchers.IO) {
        try
        {
            val chapter = ObjectBox.ayaBox!!.query(AyaEntity_.sura.equal(suraId)).build()
            val result = chapter.find()
            chapter.close()
            result
        } catch (e: Exception) {
            Log.e("****", "Error loading chapter by ID: $suraId", e)
            emptyList<AyaEntity>()
        }
    }


}