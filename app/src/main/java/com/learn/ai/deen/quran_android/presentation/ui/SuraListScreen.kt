package com.learn.ai.deen.quran_android.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learn.ai.deen.quran_android.data.model.ChapterEntity
import com.learn.ai.deen.quran_android.presentation.QuranViewModel
import com.learn.ai.deen.quran_android.presentation.Screen
import com.learn.ai.deen.quran_android.presentation.UIState
import com.learn.ai.deen.quran_android.presentation.component.SuraListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuraListScreen(backStack: SnapshotStateList<Screen>, viewModel: QuranViewModel) {
    val dataState = viewModel.dataState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quran Vector Search") },
                actions = {
                    IconButton(onClick = { backStack.add(Screen.Bookmarks) }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Bookmarks"
                        )
                    }
                    IconButton(onClick = { backStack.add(Screen.SuraSearchScreen()) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = dataState.value) {
                is UIState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading Quran data...", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                is UIState.Success -> {
                    SuraListContent(
                        suras = state.data,
                        onAction = { chapter ->
                            backStack.add(Screen.Sura(chapter))
                        }
                    )
                }

                is UIState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Failed to load Quran data.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadData() }) {
                            Text("Retry")
                        }
                    }
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

