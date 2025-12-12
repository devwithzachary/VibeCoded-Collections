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
}
