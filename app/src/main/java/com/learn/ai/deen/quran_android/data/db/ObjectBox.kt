package com.learn.ai.deen.quran_android.data.db

import android.content.Context
import com.learn.ai.deen.quran_android.data.model.AyaEntity
import com.learn.ai.deen.quran_android.data.model.ChapterEntity
import com.learn.ai.deen.quran_android.data.model.MyObjectBox
import io.objectbox.Box
import io.objectbox.BoxStore

object ObjectBox {
    private var store: BoxStore? = null

    fun init(context: Context) {
        store = MyObjectBox.builder()
            .androidContext(context)
            .build()
    }

    val ayaBox : Box<AyaEntity>?
        get() = store?.boxFor(AyaEntity::class.java)

    val chapterBox : Box<ChapterEntity>?
        get() = store?.boxFor(ChapterEntity::class.java)
}