package com.devwithzachary.collections.data

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class CollectionsRepository(
    private val collectionDao: CollectionDao,
    private val collectionItemDao: CollectionItemDao
) {
    fun getAllCollections(): Flow<List<Collection>> = collectionDao.getAllCollections()

    suspend fun insertCollection(collection: Collection) {
        collectionDao.insert(collection)
    }

    suspend fun updateCollection(collection: Collection) {
        collectionDao.update(collection)
    }

    suspend fun deleteCollection(collection: Collection) {
        collectionDao.delete(collection)
    }

    fun getItemsForCollection(collectionId: Int): Flow<List<CollectionItem>> =
        collectionItemDao.getItemsForCollection(collectionId)

    suspend fun insertCollectionItem(item: CollectionItem) {
        collectionItemDao.insert(item)
    }

    suspend fun updateCollectionItem(item: CollectionItem) {
        collectionItemDao.update(item)
    }

    suspend fun deleteCollectionItem(item: CollectionItem) {
        collectionItemDao.delete(item)
    }

    suspend fun exportData(): String = withContext(Dispatchers.IO) {
        val collections = collectionDao.getAllCollectionsSync()
        val exportList = collections.map { collection ->
            val items = collectionItemDao.getItemsForCollectionSync(collection.id)
            CollectionWithItemsExport(
                name = collection.name,
                items = items.map { item ->
                    CollectionItemExport(
                        name = item.name,
                        description = item.description,
                        has = item.has,
                        wants = item.wants,
                        imageUri = item.imageUri
                    )
                }
            )
        }
        val exportData = CollectionsExport(exportList)
        Gson().toJson(exportData)
    }

    suspend fun importData(json: String) = withContext(Dispatchers.IO) {
        val data = Gson().fromJson(json, CollectionsExport::class.java)
        data.collections.forEach { collectionExport ->
            val newCollection = Collection(name = collectionExport.name)
            val collectionId = collectionDao.insert(newCollection).toInt()
            collectionExport.items.forEach { itemExport ->
                collectionItemDao.insert(
                    CollectionItem(
                        collectionId = collectionId,
                        name = itemExport.name,
                        description = itemExport.description,
                        has = itemExport.has,
                        wants = itemExport.wants,
                        imageUri = itemExport.imageUri
                    )
                )
            }
        }
    }
}
