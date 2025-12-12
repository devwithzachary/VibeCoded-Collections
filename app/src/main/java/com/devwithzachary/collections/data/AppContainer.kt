package com.devwithzachary.collections.data

import android.content.Context

interface AppContainer {
    val collectionsRepository: CollectionsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val collectionsRepository: CollectionsRepository by lazy {
        CollectionsRepository(
            AppDatabase.getDatabase(context).collectionDao(),
            AppDatabase.getDatabase(context).collectionItemDao()
        )
    }
}
