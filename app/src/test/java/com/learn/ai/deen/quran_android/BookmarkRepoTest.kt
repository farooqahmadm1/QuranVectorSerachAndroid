package com.learn.ai.deen.quran_android

import com.learn.ai.deen.quran_android.data.model.BookmarkEntity
import com.learn.ai.deen.quran_android.data.model.MyObjectBox
import com.learn.ai.deen.quran_android.data.repo.BookmarkRepo
import io.objectbox.Box
import io.objectbox.BoxStore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class BookmarkRepoTest {

    private lateinit var store: BoxStore
    private lateinit var bookmarkBox: Box<BookmarkEntity>
    private lateinit var bookmarkRepo: BookmarkRepo
    private lateinit var tempDir: File

    @Before
    fun setUp() {
        tempDir = File.createTempFile("objectbox-bookmark-test", "")
        tempDir.delete()

        store = MyObjectBox.builder()
            .directory(tempDir)
            .build()
        bookmarkBox = store.boxFor(BookmarkEntity::class.java)
        bookmarkRepo = BookmarkRepo(bookmarkBox)
    }

    @After
    fun tearDown() {
        store.close()
        store.deleteAllFiles()
    }

    @Test
    fun testAddAndFindBookmark() {
        val added = bookmarkRepo.addBookmark(1, 1, "My favorite verse on mercy")
        assertNotNull(added)
        assertTrue(bookmarkRepo.isBookmarked(1, 1))

        val found = bookmarkRepo.findBookmark(1, 1)
        assertNotNull(found)
        assertEquals("My favorite verse on mercy", found?.note)
    }

    @Test
    fun testRemoveBookmark() {
        bookmarkRepo.addBookmark(2, 255, "Ayat al-Kursi reflection")
        assertTrue(bookmarkRepo.isBookmarked(2, 255))

        bookmarkRepo.removeBookmark(2, 255)
        assertFalse(bookmarkRepo.isBookmarked(2, 255))
        assertNull(bookmarkRepo.findBookmark(2, 255))
    }

    @Test
    fun testUpdateBookmarkNote() {
        bookmarkRepo.addBookmark(112, 1, "Initial note")
        val updated = bookmarkRepo.addBookmark(112, 1, "Updated reflection on Tawhid")

        assertNotNull(updated)
        assertEquals("Updated reflection on Tawhid", updated?.note)
        assertEquals(1, bookmarkBox.count())
    }
}
