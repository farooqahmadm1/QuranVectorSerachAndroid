package com.learn.ai.deen.quran_android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.learn.ai.deen.quran_android.data.model.ChapterEntity
import com.learn.ai.deen.quran_android.presentation.theme.QuranVectorSerachAndroidTheme
import com.learn.ai.deen.quran_android.presentation.ui.SuraListScreen
import com.learn.ai.deen.quran_android.presentation.ui.SuraScreen
import com.learn.ai.deen.quran_android.presentation.ui.search.SuraSearchScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<QuranViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuranVectorSerachAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavigationDemo(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationDemo(viewModel: QuranViewModel) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.SuraList) }
    NavDisplay(
        backStack = backStack, onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                Screen.SuraList -> NavEntry(key) { SuraListScreen(backStack, viewModel) }
                is Screen.Sura -> NavEntry(key) {
                    SuraScreen(backStack, viewModel, key.chapter)
                }
                is Screen.SuraSearchScreen -> NavEntry(key) {
                    SuraSearchScreen(backStack, viewModel, key.chapter) // Assuming you want to show the same screen
                }
            }
        })
}


sealed class Screen() {
    data object SuraList : Screen()
    data class Sura(val chapter: ChapterEntity) : Screen()
    data class SuraSearchScreen(val chapter: ChapterEntity? = null) : Screen()
}

