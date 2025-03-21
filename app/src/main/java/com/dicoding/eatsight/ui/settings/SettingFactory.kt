package com.dicoding.eatsight.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingFactory(private val pref: SettingPreferences): ViewModelProvider.NewInstanceFactory() {
    @Suppress ("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            return SettingsViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknow ViewModel class: " + modelClass.name)
    }
}