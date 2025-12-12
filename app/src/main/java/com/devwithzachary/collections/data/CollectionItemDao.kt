package com.devwithzachary.collections.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionItemDao {
    @Query("SELECT * FROM collection_items WHERE collectionId = :collectionId ORDER BY name ASC")
    fun getItemsForCollection(collectionId: Int): Flow<List<CollectionItem>>

    @Query("SELECT * FROM collection_items WHERE collectionId = :collectionId")
    suspend fun getItemsForCollectionSync(collectionId: Int): List<CollectionItem>

    @Insert
    suspend fun insert(item: CollectionItem)

    @Update
    suspend fun update(item: CollectionItem)

    @Delete
    suspend fun delete(item: CollectionItem)
}
