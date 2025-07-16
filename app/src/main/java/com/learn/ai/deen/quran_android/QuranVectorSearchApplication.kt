package com.learn.ai.deen.quran_android

import android.app.Application
import com.learn.ai.deen.quran_android.data.db.ObjectBox
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QuranVectorSearchApplication : Application() {

    companion object {
        lateinit var instance: QuranVectorSearchApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ObjectBox.init(this)
        // Initialize any libraries or components here if needed
    }
}