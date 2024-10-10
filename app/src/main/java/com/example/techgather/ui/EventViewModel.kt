package com.example.techgather.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.techgather.data.event.DetailEventResponse
import com.example.techgather.data.event.Event
import com.example.techgather.data.event.EventResponse
import com.example.techgather.data.event.ListEventsItem
import com.example.techgather.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventViewModel : ViewModel() {

    private var isDataLoaded = false
    var isFinishedEventsLoaded = false

    private val _activeEvents = MutableLiveData<List<ListEventsItem>>()
    val activeEvents: LiveData<List<ListEventsItem>> get() = _activeEvents

    private val _listEvent = MutableLiveData<List<ListEventsItem>>()
    val listEvent: LiveData<List<ListEventsItem>> get() = _listEvent

    private val _eventDetail = MutableLiveData<Event?>()
    val eventDetail: LiveData<Event?> get() = _eventDetail

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchInactiveEventData()
        fetchActiveEvents()
    }

    // Load finished events when the fragment is initialized
    fun loadFinishedEvents() {
        _isLoading.value = true
        ApiConfig.getApiService().getInactiveEvents().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listEvent.value = response.body()?.listEvents ?: emptyList()
                    isFinishedEventsLoaded = true
                } else {
                    _errorMessage.value = "Failed to load finished events"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                if (!isFinishedEventsLoaded) {  // Only show error if finished events not loaded
                    _errorMessage.value = "Error: ${t.message}"
                }
            }
        })
    }

    // Search finished events based on query
    fun searchFinishedEvents(keyword: String) {
        _isLoading.value = true
        ApiConfig.getApiService().searchEvents(keyword).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listEvent.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to search events"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    // Fetching active events
    fun fetchActiveEvents() {
        if (!isDataLoaded) {
            _isLoading.value = true
            ApiConfig.getApiService().getActiveEvents().enqueue(object : Callback<EventResponse> {
                override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _activeEvents.value = response.body()?.listEvents ?: emptyList()
                        isDataLoaded = true
                    } else {
                        _errorMessage.value = "Failed to load active events"
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    _isLoading.value = false
                    if (!isDataLoaded) {  // Only show error if active events not loaded
                        _errorMessage.value = "Error fetching active events: ${t.message}"
                    }
                }
            })
        }
    }

    // Fetching inactive events
    private fun fetchInactiveEventData() {
        _isLoading.value = true  // Tampilkan progress bar
        ApiConfig.getApiService().getInactiveEvents().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    _listEvent.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load inactive events: ${response.message()}"
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _errorMessage.value = "Error fetching inactive events: ${t.message}"
                _isLoading.value = false
            }
        })
    }

    // Fetching event details
    fun fetchDetailEvent(eventId: String) {
        ApiConfig.getApiService().getDetailEvent(eventId).enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                if (response.isSuccessful) {
                    val detailResponse = response.body()
                    if (detailResponse != null && detailResponse.error != true) {
                        _eventDetail.value = detailResponse.event
                    } else {
                        _errorMessage.value = detailResponse?.message ?: "Event not found"
                    }
                } else {
                    _errorMessage.value = "Failed to load event details: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                _errorMessage.value = "Error fetching event details: ${t.message}"
            }
        })
    }

}
