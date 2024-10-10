package com.example.techgather.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.techgather.R
import com.example.techgather.databinding.ActivityHomeBinding
import com.example.techgather.ui.ui.setting.SettingPreferences
import com.example.techgather.ui.ui.setting.dataStore
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var settingPreferences: SettingPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up navigation components
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_home) as NavHostFragment
        val navController = navHostFragment.navController

        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)

        // Load theme settings from SettingPreferences
        settingPreferences = SettingPreferences.getInstance(this.dataStore)

        // Apply theme before fragment rendering
        applyThemeSettings()
    }

    private fun applyThemeSettings() {
        // Use lifecycleScope to fetch theme setting
        lifecycleScope.launch {
            val isDarkModeActive = settingPreferences.getThemeSetting().first()
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
