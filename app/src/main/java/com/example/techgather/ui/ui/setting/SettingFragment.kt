package com.example.techgather.ui.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.techgather.R
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.concurrent.TimeUnit


class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = view.findViewById<SwitchMaterial>(R.id.switch_theme)
        val switchDaily = view.findViewById<SwitchMaterial>(R.id.switch_daily)

        val pref = SettingPreferences.getInstance(requireContext().dataStore)

        val settingViewModel = ViewModelProvider(this, ViewModelFactory(pref))[SettingViewModel::class.java]

        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        settingViewModel.getNotificationSetting().observe(viewLifecycleOwner) { isNotificationEnabled: Boolean ->
            switchDaily.isChecked = isNotificationEnabled
        }

        switchDaily.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveNotificationSetting(isChecked)
            if (isChecked) {
                startDailyReminder()
            } else {
                cancelDailyReminder()
            }
        }
    }

    private fun startDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "DailyReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun cancelDailyReminder() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyReminder")
    }
}