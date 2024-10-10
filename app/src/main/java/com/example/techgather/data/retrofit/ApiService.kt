package com.example.techgather.data.retrofit

import com.example.techgather.data.event.DetailEventResponse
import com.example.techgather.data.event.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events?active=0")
    fun getInactiveEvents(): Call<EventResponse>

    // Event yang aktif (akan datang)
    @GET("events?active=1")
    fun getActiveEvents(): Call<EventResponse>

    // Search event berdasarkan keyword
    @GET("events?active=-1")
    fun searchEvents(@Query("q") keyword: String): Call<EventResponse>

    // Detail event berdasarkan ID
    @GET("events/{id}")
    fun getDetailEvent(@Path("id") id: String): Call<DetailEventResponse>

    @GET("events?active=-1")
    fun getEventDaily(@Query("limit") limit: Int = 1): Call<EventResponse>
}

