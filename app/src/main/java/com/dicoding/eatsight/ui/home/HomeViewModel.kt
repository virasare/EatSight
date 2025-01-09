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

    private val _classificationResult = MutableLiveData<Pair<String, Float>>()
    val classificationResult: LiveData<Pair<String, Float>> = _classificationResult

    fun classificationResult(result: String, confidence: Float) {
        _classificationResult.value = Pair(result, confidence)
    }

    fun setProgressVisibility(visibility: Int) {
        _progressVisibility.value = visibility
    }

    // Function to update current image URI
    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }
}
