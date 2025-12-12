package com.devwithzachary.collections.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "collection_items",
    foreignKeys = [
        ForeignKey(
            entity = Collection::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CollectionItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val collectionId: Int,
    val name: String,
    val description: String,
    val has: Boolean,
    val wants: Boolean
)
