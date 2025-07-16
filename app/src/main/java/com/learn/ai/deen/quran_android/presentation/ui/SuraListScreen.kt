package com.learn.ai.deen.quran_android.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learn.ai.deen.quran_android.data.model.ChapterEntity
import com.learn.ai.deen.quran_android.presentation.QuranViewModel
import com.learn.ai.deen.quran_android.presentation.Screen
import com.learn.ai.deen.quran_android.presentation.UIState
import com.learn.ai.deen.quran_android.presentation.component.SuraListItem

@Composable
fun SuraListScreen(backStack: SnapshotStateList<Screen>, viewModel: QuranViewModel) {
    val dataState = viewModel.dataState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
//            var textFieldState by rememberSaveable { mutableStateOf(TextFieldState()) }
//            var searchResults by rememberSaveable { mutableStateOf(emptyList<String>()) }
//            Box {
//                SimpleSearchBar(
//                    textFieldState = textFieldState,
//                    onSearch = { query ->
//                        // Perform search logic here
////                        searchResults = viewModel.searchSuras(query)
//                    },
//                    searchResults = searchResults,
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = dataState.value) {
                is UIState.Loading -> {
                    // Show loading indicator
                }

                is UIState.Success -> {
                    // Display the list of Suras
                    SuraListContent(
                        suras = state.data,
                        onAction = { chapter ->
                            // Navigate to SuraScreen with the selected chapter
                            backStack.add(Screen.Sura(chapter))
                        }
                    )
                }

                is UIState.Error -> {
                    // Show error message
                }
            }
        }
    }
}

@Composable
fun SuraListContent(
    suras: List<ChapterEntity>,
    onAction: (ChapterEntity) -> Unit
) {
    LazyColumn {
        items(suras) { chapter ->
            SuraListItem(chapter = chapter, onClick = { onAction(chapter) })
        }
    }
}

