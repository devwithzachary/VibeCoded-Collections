package com.devwithzachary.collections.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY name ASC")
    fun getAllCollections(): Flow<List<Collection>>

    @Insert
    suspend fun insert(collection: Collection)

    @Update
    suspend fun update(collection: Collection)

    @Delete
    suspend fun delete(collection: Collection)
}
