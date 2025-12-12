package com.devwithzachary.collections

import android.app.Application
import com.devwithzachary.collections.data.AppContainer
import com.devwithzachary.collections.data.AppDataContainer

class CollectionsApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
