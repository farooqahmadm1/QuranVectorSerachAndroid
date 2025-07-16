package com.learn.ai.deen.quran_android.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChapterModel(
    @SerialName("sura")
    val sura: Long,
    @SerialName("ayas_count")
    val ayasCount: Long,
    @SerialName("first_aya_id")
    val firstAyaId: Long,
    @SerialName("name_arabic")
    val nameArabic: String,
    @SerialName("name_transliteration")
    val nameTransliteration: String,
    @SerialName("type")
    val type: String,
    @SerialName("revelation_order")
    val revelationOrder: Long,
    @SerialName("rukus")
    val rukus: Long
)