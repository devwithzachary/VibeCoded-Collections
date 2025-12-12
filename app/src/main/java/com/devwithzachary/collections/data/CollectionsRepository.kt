package com.devwithzachary.collections.data

import kotlinx.coroutines.flow.Flow

class CollectionsRepository(
    private val collectionDao: CollectionDao,
    private val collectionItemDao: CollectionItemDao
) {
    fun getAllCollections(): Flow<List<Collection>> = collectionDao.getAllCollections()

    suspend fun insertCollection(collection: Collection) {
        collectionDao.insert(collection)
    }

    fun getItemsForCollection(collectionId: Int): Flow<List<CollectionItem>> =
        collectionItemDao.getItemsForCollection(collectionId)

    suspend fun insertCollectionItem(item: CollectionItem) {
        collectionItemDao.insert(item)
    }

    suspend fun updateCollectionItem(item: CollectionItem) {
        collectionItemDao.update(item)
    }
}
