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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
    val bookmarkedKeys by viewModel.bookmarkedKeys.collectAsStateWithLifecycle()

    val currentPlayingAya by viewModel.currentPlayingAya.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val isAudioLoading by viewModel.isAudioLoading.collectAsStateWithLifecycle()

    var bookmarkDialogAya by remember { mutableStateOf<AyaEntity?>(null) }

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
        },
        bottomBar = {
            if (currentPlayingAya != null) {
                AudioPlayerBottomBar(
                    currentAya = currentPlayingAya!!,
                    isPlaying = isAudioPlaying,
                    isLoading = isAudioLoading,
                    onPlayPause = {
                        if (isAudioPlaying) viewModel.pauseAudio() else viewModel.resumeAudio()
                    },
                    onNext = { viewModel.playNextAudio() },
                    onPrevious = { viewModel.playPreviousAudio() },
                    onClose = { viewModel.stopAudio() }
                )
            }
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
                bookmarkedKeys = bookmarkedKeys,
                currentPlayingAya = currentPlayingAya,
                isAudioPlaying = isAudioPlaying,
                onShowTafsir = { aya -> viewModel.setTafsirAya(aya) },
                onToggleBookmark = { aya ->
                    val key = "${aya.sura}:${aya.aya}"
                    if (bookmarkedKeys.contains(key)) {
                        viewModel.toggleBookmark(aya.sura, aya.aya)
                    } else {
                        bookmarkDialogAya = aya
                    }
                },
                onPlayAudio = { aya ->
                    if (currentPlayingAya?.sura == aya.sura && currentPlayingAya?.aya == aya.aya && isAudioPlaying) {
                        viewModel.pauseAudio()
                    } else {
                        viewModel.playAyaAudio(aya, ayas)
                    }
                }
            )
        }

        selectedTafsirAya?.let { aya ->
            TafsirDialog(
                aya = aya,
                onDismiss = { viewModel.setTafsirAya(null) }
            )
        }

        bookmarkDialogAya?.let { aya ->
            AddBookmarkNoteDialog(
                sura = aya.sura,
                aya = aya.aya,
                onDismiss = { bookmarkDialogAya = null },
                onSave = { note ->
                    viewModel.toggleBookmark(aya.sura, aya.aya, note)
                    bookmarkDialogAya = null
                }
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
    bookmarkedKeys: Set<String>,
    currentPlayingAya: AyaEntity?,
    isAudioPlaying: Boolean,
    onShowTafsir: (AyaEntity) -> Unit,
    onToggleBookmark: (AyaEntity) -> Unit,
    onPlayAudio: (AyaEntity) -> Unit
) {
    LazyColumn {
        items(ayas) { aya ->
            val isBookmarked = bookmarkedKeys.contains("${aya.sura}:${aya.aya}")
            val isCurrentPlaying = currentPlayingAya?.sura == aya.sura && currentPlayingAya?.aya == aya.aya
            AyaCardItem(
                aya = aya,
                translationMode = translationMode,
                isBookmarked = isBookmarked,
                isCurrentPlaying = isCurrentPlaying,
                isAudioPlaying = isAudioPlaying,
                onShowTafsir = { onShowTafsir(aya) },
                onToggleBookmark = { onToggleBookmark(aya) },
                onPlayAudio = { onPlayAudio(aya) }
            )
        }
    }
}

@Composable
fun AyaCardItem(
    aya: AyaEntity,
    translationMode: TranslationMode,
    isBookmarked: Boolean,
    isCurrentPlaying: Boolean,
    isAudioPlaying: Boolean,
    onShowTafsir: () -> Unit,
    onToggleBookmark: () -> Unit,
    onPlayAudio: () -> Unit
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
            Row {
                IconButton(onClick = onPlayAudio) {
                    Icon(
                        imageVector = if (isCurrentPlaying && isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Audio Recitation",
                        tint = if (isCurrentPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = onShowTafsir) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Tafsir",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
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
            color = if (isCurrentPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
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
fun AudioPlayerBottomBar(
    currentAya: AyaEntity,
    isPlaying: Boolean,
    isLoading: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Surah ${currentAya.sura}, Verse ${currentAya.aya}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = currentAya.text ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close Player", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
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

@Composable
fun AddBookmarkNoteDialog(
    sura: Long,
    aya: Long,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bookmark Verse $sura:$aya") },
        text = {
            Column {
                Text("Add an optional reflection or note for this bookmark:", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Custom Note") },
                    placeholder = { Text("e.g. Favorite verse on patience...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(noteText) }) {
                Text("Save Bookmark")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
