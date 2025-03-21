package com.dicoding.eatsight

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.eatsight.databinding.ActivityMainBinding
import com.dicoding.eatsight.ui.settings.SettingFactory
import com.dicoding.eatsight.ui.settings.SettingPreferences
import com.dicoding.eatsight.ui.settings.SettingsViewModel
import com.dicoding.eatsight.ui.settings.dataStore
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.MaterialColors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setWindowBackground()

        setupNavigation()

        setupThemeSettings()
    }

    private fun setWindowBackground() {
        val backgroundColor = TypedValue()
        theme.resolveAttribute(android.R.attr.windowBackground, backgroundColor, true)
        window.decorView.setBackgroundColor(backgroundColor.data)
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_history, R.id.navigation_settings)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        updateBottomNavColors(navView)
    }

    private fun setupThemeSettings() {
        val pref = SettingPreferences.getInstance(applicationContext.dataStore)
        settingViewModel = ViewModelProvider(this, SettingFactory(pref))[SettingsViewModel::class.java]

        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun updateBottomNavColors(navView: BottomNavigationView) {
        val backgroundColor = MaterialColors.getColor(navView, com.google.android.material.R.attr.colorSurface)
        val iconTextColor = MaterialColors.getColor(navView, com.google.android.material.R.attr.colorOnSurface)

        navView.setBackgroundColor(backgroundColor)
        navView.itemIconTintList = ColorStateList.valueOf(iconTextColor)
        navView.itemTextColor = ColorStateList.valueOf(iconTextColor)
    }
}
