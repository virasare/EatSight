package com.dicoding.eatsight.ui.result

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.dicoding.eatsight.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        val resultText = intent.getStringExtra(EXTRA_RESULT)

        imageUri?.let {
            val uri = Uri.parse(it)
            binding.resultImage.setImageURI(uri)
        }

        binding.resultHere.text = resultText ?: "No result"
    }
}
