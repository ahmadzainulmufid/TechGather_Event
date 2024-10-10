package com.example.techgather.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.techgather.data.database.FavoriteDao
import com.example.techgather.data.database.FavoriteEvent
import com.example.techgather.data.database.FavoriteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteRepository(application: Application) {
    private val favoriteDao: FavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteRoomDatabase.getDatabase(application)
        favoriteDao = db.favoriteDao()
    }

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent> {
        return favoriteDao.getFavoriteEventById(id)
    }

    fun insertFavorite(favoriteEvent: FavoriteEvent) {
        executorService.execute { favoriteDao.insertFavorite(favoriteEvent) }
    }

    fun deleteFavorite(favoriteEvent: FavoriteEvent) {
        executorService.execute { favoriteDao.deleteFavorite(favoriteEvent) }
    }

    fun getFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return favoriteDao.getAllFavoriteEvents()
    }
}