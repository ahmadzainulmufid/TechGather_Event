package com.example.techgather.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(favoriteEvent: FavoriteEvent)

    @Delete
    fun deleteFavorite(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM FavoriteEvent WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent>

    @Query("SELECT * FROM FavoriteEvent")
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>>
}