package com.learn.ai.deen.quran_android

import com.learn.ai.deen.quran_android.data.model.AyaEntity
import com.learn.ai.deen.quran_android.data.model.AyaEntity_
import com.learn.ai.deen.quran_android.data.model.MyObjectBox
import io.objectbox.Box
import io.objectbox.BoxStore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class VectorSearchLogicTest {

    private lateinit var store: BoxStore
    private lateinit var ayaBox: Box<AyaEntity>
    private lateinit var tempDir: File

    @Before
    fun setUp() {
        tempDir = File.createTempFile("objectbox-test", "")
        tempDir.delete()

        store = MyObjectBox.builder()
            .directory(tempDir)
            .build()
        ayaBox = store.boxFor(AyaEntity::class.java)
    }

    @After
    fun tearDown() {
        store.close()
        store.deleteAllFiles()
    }

    @Test
    fun testVectorSearchNearestNeighborsQuery() {
        // Create 256-dimensional vector for Al-Fatiha verse 1
        val vector1 = FloatArray(256) { 0.1f }
        vector1[0] = 0.9f // Peak dimension for verse 1

        // Create 256-dimensional vector for Al-Ikhlas verse 1
        val vector2 = FloatArray(256) { 0.1f }
        vector2[1] = 0.9f // Peak dimension for verse 2

        val aya1 = AyaEntity(
            sura = 1,
            aya = 1,
            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
            embedding = vector1
        )

        val aya2 = AyaEntity(
            sura = 112,
            aya = 1,
            text = "قُلْ هُوَ ٱللَّهُ أَحَدٌ",
            embedding = vector2
        )

        ayaBox.put(aya1, aya2)
        assertEquals(2, ayaBox.count())

        // Query vector close to vector1
        val queryVector = FloatArray(256) { 0.1f }
        queryVector[0] = 0.85f

        val query = ayaBox.query(AyaEntity_.embedding.nearestNeighbors(queryVector, 2)).build()
        val results = query.find()
        query.close()

        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        // Top nearest result should be Al-Fatiha (sura 1, aya 1)
        assertEquals(1L, results[0].sura)
        assertEquals(1L, results[0].aya)
    }
}
