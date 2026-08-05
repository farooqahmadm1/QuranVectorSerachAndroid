package com.learn.ai.deen.quran_android.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AyaModel(
    val sura: Long,
    val aya: Long,
    val text: String,
    val md5: String? = null,
    val translationEn: String? = null,
    val translationUr: String? = null,
    val tafsir: String? = null,
    val embedding: List<Double>
)