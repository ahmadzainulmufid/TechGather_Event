package com.example.techgather.ui.ui.setting

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.techgather.R
import com.example.techgather.data.event.EventResponse
import com.example.techgather.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log

class DailyReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Panggil API untuk mendapatkan event terdekat
        val apiService = ApiConfig.getApiService()
        apiService.getEventDaily(1).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val event = response.body()?.listEvents?.firstOrNull()
                    event?.let {
                        sendNotification(it.name ?: "Event Terdekat", it.beginTime ?: "Waktu Tidak Diketahui")
                    }
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Log.e("DailyReminderWorker", "Failed to fetch event data", t)

                sendFailureNotification()
            }
        })

        return Result.success()
    }

    private fun sendNotification(eventName: String, eventDate: String) {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notificationManager = NotificationManagerCompat.from(applicationContext)

            if (Build.VERSION.SDK_INT >= 33) {
                val channel = NotificationChannel(
                    "daily_reminder_channel",
                    "Daily Reminder",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(applicationContext, "daily_reminder_channel")
                .setContentTitle("Event Terdekat: $eventName")
                .setContentText("Waktu: $eventDate")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            notificationManager.notify(1, notification)
        }
    }

    private fun sendFailureNotification() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notificationManager = NotificationManagerCompat.from(applicationContext)

            if (Build.VERSION.SDK_INT >= 33) {
                val channel = NotificationChannel(
                    "daily_reminder_channel",
                    "Daily Reminder",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notification =
                NotificationCompat.Builder(applicationContext, "daily_reminder_channel")
                    .setContentTitle("Reminder Error")
                    .setContentText("Failed to fetch event data")
                    .setSmallIcon(R.drawable.baseline_error_24)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()

            notificationManager.notify(2, notification)
        }
    }
}
