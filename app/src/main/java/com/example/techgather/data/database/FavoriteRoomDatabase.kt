package com.example.techgather.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteEvent::class], version = 1, exportSchema = false)
abstract class FavoriteRoomDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): FavoriteRoomDatabase {
           if (INSTANCE == null) {
               synchronized(FavoriteRoomDatabase::class.java) {
                   INSTANCE = Room.databaseBuilder(
                       context.applicationContext,
                       FavoriteRoomDatabase::class.java, "favorite_event_database"
                   ).build()
               }
           }
            return INSTANCE as FavoriteRoomDatabase
        }
    }
}