package com.learn.ai.deen.quran_android.data.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class ChapterEntity (
    @Id var id: Long = 0,
    var sura: Long,
    var ayasCount: Long,
    var firstAyaId: Long,
    var nameArabic: String = "",
    var nameTransliteration: String = "",
    var type: String = "",
    var revelationOrder: Long = 0,
    var rukus: Long = 0
)