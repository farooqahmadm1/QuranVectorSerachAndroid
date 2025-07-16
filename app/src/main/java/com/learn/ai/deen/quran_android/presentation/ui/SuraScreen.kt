package com.learn.ai.deen.quran_android.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.learn.ai.deen.quran_android.data.model.AyaEntity
import com.learn.ai.deen.quran_android.presentation.QuranViewModel
import com.learn.ai.deen.quran_android.presentation.Screen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import com.learn.ai.deen.quran_android.data.model.ChapterEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuraScreen(snapShot: SnapshotStateList<Screen>, viewModel: QuranViewModel, chapter: ChapterEntity) {
    var ayas by remember { mutableStateOf(emptyList<AyaEntity>()) }
    LaunchedEffect(chapter.id) {
        ayas = viewModel.loadSuraById(chapter.id)
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = { Text(text = chapter.nameTransliteration) },
                navigationIcon = {
                    IconButton(onClick = {
                        snapShot.removeLastOrNull()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { snapShot.add(Screen.SuraSearchScreen(chapter)) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            SuraContent(ayas = ayas)
        }
    }
}

@Composable
fun SuraContent(ayas: List<AyaEntity>) {
    LazyColumn {
        items(ayas) {
            val ayaText = it.text ?: ""
            val ayaNumber = it.aya
            val ayaId = it.id

            // Display each Aya in the Sura
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        isTraversalGroup = true
                        traversalIndex = ayaId.toFloat()
                    },
                headlineContent = {
                    Text(text = "$ayaText", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                },
                trailingContent = {
                    Text(text = "$ayaNumber")
                }
            )
        }
    }
}

