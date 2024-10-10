package com.example.techgather.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.HtmlCompat
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.example.techgather.R
import com.example.techgather.data.database.FavoriteEvent
import com.example.techgather.data.event.Event
import com.example.techgather.data.repository.FavoriteRepository
import com.example.techgather.ui.ui.setting.SettingPreferences
import com.example.techgather.ui.ui.setting.dataStore

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var ivMediaCover: ImageView
    private lateinit var tvEventName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvOwnerName: TextView
    private lateinit var btnRegister: Button
    private lateinit var tvBeginTime: TextView
    private lateinit var tvRemainingQuota: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var settingPreferences: SettingPreferences
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var favoriteEvent: FavoriteEvent
    private var isFavorite: Boolean = false

    private val viewModel: EventViewModel by viewModels()
    private var currentEvent: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Initialize ActionBar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        ivMediaCover = findViewById(R.id.ivMediaCover)
        tvEventName = findViewById(R.id.tvEventName)
        tvDescription = findViewById(R.id.tvDescription)
        tvOwnerName = findViewById(R.id.tvOwnerName)
        btnRegister = findViewById(R.id.btnRegister)
        tvBeginTime = findViewById(R.id.tvBeginTime)
        tvRemainingQuota = findViewById(R.id.tvRemainingQuota)
        progressBar = findViewById(R.id.progressBar)

        btnRegister.setOnClickListener(this)

        val eventId = intent.getStringExtra("EVENT_ID")

        favoriteRepository = FavoriteRepository(application)

        if (eventId != null) {
            // Tampilkan ProgressBar saat mulai memuat data
            progressBar.visibility = View.VISIBLE

            // Fetch detail event
            viewModel.fetchDetailEvent(eventId)

            // Observe the event detail LiveData
            viewModel.eventDetail.observe(this) { event ->
                progressBar.visibility = View.GONE
                event?.let {
                    currentEvent = it
                    displayEventDetails(it)

                    // Create FavoriteEvent with retrieved details
                    favoriteEvent = FavoriteEvent(
                        id = it.id.toString(),
                        name = it.name ?: "unknown_name",
                        mediaCover = it.mediaCover ?: "",
                        summary = it.summary ?: ""
                    )

                    // Observe favorite status after displaying event details
                    observeFavoriteStatus(eventId)
                }
            }

            // Observe error messages
            viewModel.errorMessage.observe(this) { message ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        settingPreferences = SettingPreferences.getInstance(application.dataStore)

        // Amati pengaturan tema
        settingPreferences.getThemeSetting().asLiveData().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Set the Favorite button click listener
        findViewById<ImageButton>(R.id.btnFavorite).setOnClickListener {
            handleFavoriteClick()
        }
    }

    // Update observeFavoriteStatus function to reset isFavorite
    private fun observeFavoriteStatus(eventId: String) {
        favoriteRepository.getFavoriteEventById(eventId).observe(this) { favoriteEvent ->
            // Reset the favorite state for the new event
            isFavorite = favoriteEvent != null
            updateFavoriteIcon(isFavorite)
        }
    }

    private fun displayEventDetails(event: Event) {
        // Update TextView with event details
        tvEventName.text = event.name
        tvOwnerName.text = event.ownerName
        tvBeginTime.text = event.beginTime

        // Use HtmlCompat to set the description
        tvDescription.text = HtmlCompat.fromHtml(event.description.orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY)

        // Calculate remaining quota
        val remainingQuota = event.quota?.minus(event.registrants ?: 0) ?: 0
        tvRemainingQuota.text = getString(R.string.remaining_quota_label, remainingQuota)

        // Update ActionBar title with event name
        supportActionBar?.title = event.name

        // Load event image using Glide
        Glide.with(this)
            .load(event.mediaCover ?: event.imageLogo)
            .into(ivMediaCover)
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val favoriteButton = findViewById<ImageButton>(R.id.btnFavorite)
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24)
        }
    }

    private fun handleFavoriteClick() {
        if (isFavorite) {
            // Favorite already exists, delete it
            favoriteRepository.deleteFavorite(favoriteEvent)
        } else {
            // Add to favorites
            favoriteRepository.insertFavorite(favoriteEvent)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnRegister -> {
                val url = currentEvent?.link
                if (url != null) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Registration URL not available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Handle back button in ActionBar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
