package com.learn.ai.deen.quran_android.domain

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder.TextEmbedderOptions

class QueryVectorizer(private val context: Context) {

    private var textEmbedder: TextEmbedder? = null
    private val modelPath = "vector_search_model.tflite"

    init {
        initializeTextEmbedder()
    }

    private fun initializeTextEmbedder() {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelPath)
                .build()

            val options = TextEmbedderOptions.builder()
                .setBaseOptions(baseOptions)
                .build()

            textEmbedder = TextEmbedder.createFromOptions(context, options)
            Log.i("QueryVectorizer", "TextEmbedder initialized successfully.")
        } catch (e: Exception) {
            Log.e("QueryVectorizer", "Error initializing TextEmbedder: ${e.message}", e)
        }
    }

    /**
     * Converts a query text into an embedding vector.
     * @param queryText The input text to convert.
     * @return A FloatArray representing the embedding, or null if an error occurs.
     */
    fun getQueryEmbedding(queryText: String): FloatArray? {
        if (textEmbedder == null) {
            Log.e("QueryVectorizer", "TextEmbedder is not initialized.")
            return null
        }

        return try {
            // Perform inference
            val embeddingResult = textEmbedder?.embed(queryText)

            // Get the first (and usually only) embedding from the result
            embeddingResult?.embeddingResult()?.embeddings()?.first()?.floatEmbedding()

        } catch (e: Exception) {
            Log.e("QueryVectorizer", "Error getting embedding for query: $queryText - ${e.message}", e)
            null
        }
    }

    /**
     * Closes the TextEmbedder to release resources. Call this when the component
     * (e.g., Activity, Fragment) using it is destroyed.
     */
    fun close() {
        textEmbedder?.close()
        textEmbedder = null
        Log.i("QueryVectorizer", "TextEmbedder closed.")
    }
}