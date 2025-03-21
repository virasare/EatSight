package com.dicoding.eatsight.ui.result

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.dicoding.eatsight.MainActivity
import com.dicoding.eatsight.R
import com.dicoding.eatsight.data.AppDatabase
import com.dicoding.eatsight.data.HistoryEntity
import com.dicoding.eatsight.data.HistoryRepository
import com.dicoding.eatsight.databinding.ActivityResultBinding
import com.dicoding.eatsight.ui.history.HistoryFragment
import com.dicoding.eatsight.ui.history.HistoryViewModel
import com.dicoding.eatsight.ui.history.HistoryViewModelFactory

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }

    private lateinit var binding: ActivityResultBinding

    private val historyViewModel: HistoryViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = HistoryRepository(database.historyDao())
        HistoryViewModelFactory(repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        val resultText = intent.getStringExtra(EXTRA_RESULT)

        imageUri?.let {
            val uri = Uri.parse(it)
            binding.resultImage.setImageURI(uri)

            historyViewModel.insertHistory(HistoryEntity(imageUri = it, classificationResult = resultText ?: "Unknown"))
        }

        binding.resultHere.text = resultText ?: "No result"

        setupFab()
    }

    private fun setupFab() {
        binding.fabHistory.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("navigate_to", R.id.navigation_history)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }
    }

}
