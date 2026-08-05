package com.learn.ai.deen.quran_android

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.learn.ai.deen.quran_android.domain.QueryVectorizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VectorSearchInstrumentedTest {

    @Test
    fun testQueryVectorizerEmbeddingGeneration() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val vectorizer = QueryVectorizer(appContext)

        val queryText = "mercy"
        val embedding = vectorizer.getQueryEmbedding(queryText)

        assertNotNull("Query embedding should not be null", embedding)
        assertEquals("Embedding vector should have 256 dimensions", 256, embedding?.size)

        vectorizer.close()
    }
}
