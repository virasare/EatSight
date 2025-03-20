package com.dicoding.eatsight.ui.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    private val _progressVisibility = MutableLiveData<Int>()
    val progressVisibility: LiveData<Int> = _progressVisibility

    fun setProgressVisibility(visibility: Int) {
        _progressVisibility.value = visibility
    }

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }
}