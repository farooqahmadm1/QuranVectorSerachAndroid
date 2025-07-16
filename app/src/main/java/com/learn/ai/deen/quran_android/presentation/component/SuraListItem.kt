package com.learn.ai.deen.quran_android.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.learn.ai.deen.quran_android.data.model.ChapterEntity

@Composable
fun SuraListItem(
    chapter: ChapterEntity,
    onClick: () -> Unit
) {
    // Implement the UI for each Sura item
    // For example, you can use a Text composable to display the chapter name
    // and handle clicks to navigate to the SuraScreen
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = chapter.nameArabic)
        Text(text = chapter.nameTransliteration)
    }
}