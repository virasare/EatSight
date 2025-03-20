package com.dicoding.eatsight.ui.home

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _currentImage = MutableLiveData<Bitmap?>()
    val currentImage: LiveData<Bitmap?> = _currentImage

    private val _progressVisibility = MutableLiveData<Int>()
    val progressVisibility: LiveData<Int> = _progressVisibility

    fun setProgressVisibility(visibility: Int) {
        _progressVisibility.value = visibility
    }

    fun setCurrentImage(bitmap: Bitmap?) {
        _currentImage.value = bitmap
    }
}
