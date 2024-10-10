package com.example.techgather.ui.ui.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techgather.data.database.FavoriteEvent
import com.example.techgather.data.repository.FavoriteRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: FavoriteRepository) : ViewModel() {

    // LiveData untuk daftar event favorit
    private val _favoriteEvents = MediatorLiveData<List<FavoriteEvent>>()
    val favoriteEvents: LiveData<List<FavoriteEvent>> get() = _favoriteEvents

    // LiveData untuk indikator loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData untuk pesan error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    init {
        fetchFavorites()
    }

    private fun fetchFavorites() {
        _isLoading.value = true
        Log.d("FavoriteViewModel", "Start loading favorites")
        viewModelScope.launch {
            try {
                val liveData = repository.getFavoriteEvents()
                _favoriteEvents.addSource(liveData) { data ->
                    _favoriteEvents.value = data
                    _isLoading.value = false

                    if (data.isEmpty()) {
                        _favoriteEvents.value = emptyList() // Ensure LiveData is set to an empty list
                        Log.d("FavoriteViewModel", "Favorites list is empty")
                    }
                    Log.d("FavoriteViewModel", "Favorites loaded")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
                Log.d("FavoriteViewModel", "Error loading favorites: ${e.message}")
            }
        }
    }

}