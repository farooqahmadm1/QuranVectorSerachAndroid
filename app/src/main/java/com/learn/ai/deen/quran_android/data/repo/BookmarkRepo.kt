package com.learn.ai.deen.quran_android.data.repo

import com.learn.ai.deen.quran_android.data.db.ObjectBox
import com.learn.ai.deen.quran_android.data.model.BookmarkEntity
import com.learn.ai.deen.quran_android.data.model.BookmarkEntity_
import io.objectbox.Box

class BookmarkRepo(
    private val customBox: Box<BookmarkEntity>? = null
) {
    private val box: Box<BookmarkEntity>?
        get() = customBox ?: ObjectBox.bookmarkBox

    fun getAllBookmarks(): List<BookmarkEntity> {
        val targetBox = box ?: return emptyList()
        val query = targetBox.query().orderDesc(BookmarkEntity_.timestamp).build()
        val result = query.find()
        query.close()
        return result
    }

    fun addBookmark(sura: Long, aya: Long, note: String): BookmarkEntity? {
        val targetBox = box ?: return null
        val existing = findBookmark(sura, aya)
        if (existing != null) {
            existing.note = note
            existing.timestamp = System.currentTimeMillis()
            targetBox.put(existing)
            return existing
        } else {
            val newBookmark = BookmarkEntity(
                sura = sura,
                aya = aya,
                note = note,
                timestamp = System.currentTimeMillis()
            )
            targetBox.put(newBookmark)
            return newBookmark
        }
    }

    fun removeBookmark(sura: Long, aya: Long) {
        val targetBox = box ?: return
        val existing = findBookmark(sura, aya)
        if (existing != null) {
            targetBox.remove(existing)
        }
    }

    fun isBookmarked(sura: Long, aya: Long): Boolean {
        return findBookmark(sura, aya) != null
    }

    fun findBookmark(sura: Long, aya: Long): BookmarkEntity? {
        val targetBox = box ?: return null
        val query = targetBox.query(
            BookmarkEntity_.sura.equal(sura).and(BookmarkEntity_.aya.equal(aya))
        ).build()
        val result = query.findFirst()
        query.close()
        return result
    }

    fun deleteBookmarkById(id: Long) {
        box?.remove(id)
    }
}
