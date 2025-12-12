package com.devwithzachary.collections.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionItemDao {
    @Query("SELECT * FROM collection_items WHERE collectionId = :collectionId ORDER BY name ASC")
    fun getItemsForCollection(collectionId: Int): Flow<List<CollectionItem>>

    @Insert
    suspend fun insert(item: CollectionItem)
}
