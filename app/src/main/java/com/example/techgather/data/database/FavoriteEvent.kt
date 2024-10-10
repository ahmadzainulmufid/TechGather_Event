package com.example.techgather.data.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class FavoriteEvent (
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var name: String = "",
    var mediaCover: String? = null,
    var dateAdded: Long = System.currentTimeMillis(),
    var summary: String? = null
) : Parcelable