package com.devwithzachary.collections.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Collection::class, CollectionItem::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun collectionDao(): CollectionDao
    abstract fun collectionItemDao(): CollectionItemDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "collections_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
