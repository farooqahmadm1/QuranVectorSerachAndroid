package com.learn.ai.deen.quran_android.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learn.ai.deen.quran_android.data.model.AyaEntity
import com.learn.ai.deen.quran_android.data.model.ChapterEntity
import com.learn.ai.deen.quran_android.presentation.QuranViewModel
import com.learn.ai.deen.quran_android.presentation.Screen
import com.learn.ai.deen.quran_android.presentation.TranslationMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuraScreen(
    snapShot: SnapshotStateList<Screen>,
    viewModel: QuranViewModel,
    chapter: ChapterEntity
) {
    var ayas by remember { mutableStateOf(emptyList<AyaEntity>()) }
    val translationMode by viewModel.translationMode.collectAsStateWithLifecycle()
    val selectedTafsirAya by viewModel.selectedTafsirAya.collectAsStateWithLifecycle()

    LaunchedEffect(chapter.id) {
        ayas = viewModel.loadSuraById(chapter.id)
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = chapter.nameTransliteration, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${chapter.nameArabic} • ${chapter.ayasCount} Verses",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { snapShot.removeLastOrNull() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { snapShot.add(Screen.SuraSearchScreen(chapter)) }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TranslationFilterRow(
                currentMode = translationMode,
                onModeSelected = { viewModel.setTranslationMode(it) }
            )

            SuraContent(
                ayas = ayas,
                translationMode = translationMode,
                onShowTafsir = { aya -> viewModel.setTafsirAya(aya) }
            )
        }

        selectedTafsirAya?.let { aya ->
            TafsirDialog(
                aya = aya,
                onDismiss = { viewModel.setTafsirAya(null) }
            )
        }
    }
}

@Composable
fun TranslationFilterRow(
    currentMode: TranslationMode,
    onModeSelected: (TranslationMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentMode == TranslationMode.ALL,
            onClick = { onModeSelected(TranslationMode.ALL) },
            label = { Text("All") }
        )
        FilterChip(
            selected = currentMode == TranslationMode.ENGLISH,
            onClick = { onModeSelected(TranslationMode.ENGLISH) },
            label = { Text("English") }
        )
        FilterChip(
            selected = currentMode == TranslationMode.URDU,
            onClick = { onModeSelected(TranslationMode.URDU) },
            label = { Text("Urdu") }
        )
        FilterChip(
            selected = currentMode == TranslationMode.NONE,
            onClick = { onModeSelected(TranslationMode.NONE) },
            label = { Text("Arabic Only") }
        )
    }
}

@Composable
fun SuraContent(
    ayas: List<AyaEntity>,
    translationMode: TranslationMode,
    onShowTafsir: (AyaEntity) -> Unit
) {
    LazyColumn {
        items(ayas) { aya ->
            AyaCardItem(
                aya = aya,
                translationMode = translationMode,
                onShowTafsir = { onShowTafsir(aya) }
            )
        }
    }
}

@Composable
fun AyaCardItem(
    aya: AyaEntity,
    translationMode: TranslationMode,
    onShowTafsir: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${aya.aya}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onShowTafsir) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Tafsir",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Arabic text
        Text(
            text = aya.text ?: "",
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Normal
        )

        // English Translation
        if ((translationMode == TranslationMode.ALL || translationMode == TranslationMode.ENGLISH) && !aya.translationEn.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = aya.translationEn ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Urdu Translation
        if ((translationMode == TranslationMode.ALL || translationMode == TranslationMode.URDU) && !aya.translationUr.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = aya.translationUr ?: "",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun TafsirDialog(
    aya: AyaEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Tafsir - Verse ${aya.sura}:${aya.aya}")
        },
        text = {
            Column {
                Text(
                    text = aya.text ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = aya.tafsir ?: "Explanations and spiritual reflections for this verse.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
