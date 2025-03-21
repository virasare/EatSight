package com.dicoding.eatsight.ui.result

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResultViewModel : ViewModel() {

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    private val _resultText = MutableLiveData<String>()
    val resultText: LiveData<String> = _resultText

    fun setData(imageUriString: String?, result: String?) {
        _imageUri.value = Uri.parse(imageUriString)
        _resultText.value = result ?: "No result"
    }
}
