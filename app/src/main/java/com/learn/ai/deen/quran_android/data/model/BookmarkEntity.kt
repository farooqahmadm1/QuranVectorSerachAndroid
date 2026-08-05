package com.learn.ai.deen.quran_android.data.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class BookmarkEntity(
    @Id var id: Long = 0,
    var sura: Long = 0,
    var aya: Long = 0,
    var note: String = "",
    var timestamp: Long = System.currentTimeMillis()
)
