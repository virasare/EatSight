package com.dicoding.eatsight.ui.result

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.eatsight.R
import android.net.Uri
import androidx.activity.viewModels
import com.dicoding.eatsight.databinding.ActivityResultBinding
import com.opencsv.CSVReader
import java.io.InputStreamReader
import android.util.Log

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val resultViewModel: ResultViewModel by viewModels()

    // Peta ID ke Nama Makanan yang dimuat dari CSV
    private val idToNameMap: Map<String, String> by lazy {
        loadFoodMapping()
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        val resultText = intent.getStringExtra(EXTRA_RESULT)

        // Log untuk melihat ID yang diterima dari ML
        Log.d("ResultActivity", "Result Text (ID): $resultText")

        // Menampilkan gambar hasil deteksi
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            binding.resultImage.setImageURI(imageUri)
        }

        // Mengonversi ID menjadi nama makanan menggunakan peta yang dimuat dari CSV
        val foodName = resultText?.let { idToNameMap[it] } ?: getString(R.string.unknown_food)

        // Log nama makanan setelah peta
        Log.d("ResultActivity", "Food Name from ID: $foodName")

        // Menangani nilai confidence
        val confidenceScore = intent.getFloatExtra(EXTRA_CONFIDENCE_SCORE, -1f)

        if (confidenceScore != -1f) {
            val confidencePercentage = (confidenceScore * 100).toInt()
            val resultWithConfidence = getString(R.string.result_with_confidence, foodName, confidencePercentage)
            binding.resultText.text = resultWithConfidence
        } else {
            binding.resultText.text = foodName
        }

        // Pass data ke ViewModel
        resultViewModel.setData(imageUriString, resultText, confidenceScore)

        // Observasi LiveData dan update UI ketika data berubah
        resultViewModel.imageUri.observe(this) { uri ->
            uri?.let {
                binding.resultImage.setImageURI(it)
            }
        }

        // Set hasil dengan confidence dari ViewModel
        resultViewModel.getResultWithConfidence().let {
            binding.resultText.text = it
        }
    }

    // Fungsi untuk memuat mapping ID ke nama makanan dari file CSV
    private fun loadFoodMapping(): Map<String, String> {
        val inputStream = assets.open("food_mapping.csv")
        val reader = CSVReader(InputStreamReader(inputStream))
        val foodMappingList = reader.readAll()

        // Skip header jika ada
        foodMappingList.removeAt(0)

        // Log isi CSV untuk tujuan debugging
        foodMappingList.forEach {
            Log.d("FoodMapping", "ID: ${it[0]}, Name: ${it[1]}")
        }

        // Membuat peta dari ID makanan (kolom pertama) ke nama makanan (kolom kedua)
        return foodMappingList.associate {
            it[0] to it[1]  // ID (indeks 0) dan Nama (indeks 1)
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_CONFIDENCE_SCORE = "extra_confidence_score"
    }
}