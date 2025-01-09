package com.dicoding.eatsight.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.eatsight.R
import com.dicoding.eatsight.databinding.FragmentHomeBinding
import com.dicoding.eatsight.helper.ImageClassifierHelper
import com.dicoding.eatsight.ui.result.ResultActivity
import org.tensorflow.lite.task.vision.classifier.Classifications

class HomeFragment : Fragment(), ImageClassifierHelper.ClassifierListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = this
        )

        if (savedInstanceState != null) {
            currentImageUri = savedInstanceState.getParcelable(KEY_IMAGE_URI)
            val result = savedInstanceState.getString(KEY_CLASSIFICATION_RESULT)
            val confidence = savedInstanceState.getFloat(KEY_CLASSIFICATION_CONFIDENCE, -1f)

            result?.let {
                viewModel.classificationResult(it, confidence)
            }
        }

        viewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            currentImageUri = uri
            showImage()
        }

        viewModel.progressVisibility.observe(viewLifecycleOwner) { visibility ->
            binding.progressIndicator.visibility = visibility
        }

        viewModel.classificationResult.observe(viewLifecycleOwner) { result ->
            val detectedResult = result.first
            val confidenceScore = result.second

            if (confidenceScore != -1f) {
                val intent = Intent(requireContext(), ResultActivity::class.java).apply {
                    putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
                    putExtra(ResultActivity.EXTRA_RESULT, detectedResult)
                    putExtra(ResultActivity.EXTRA_CONFIDENCE_SCORE, confidenceScore)
                }
                startActivity(intent)
            } else {
                showToast(detectedResult)
            }
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
        }

        return root
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
            viewModel.setCurrentImageUri(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(uri: Uri) {
        binding.progressIndicator.visibility = View.VISIBLE
        imageClassifierHelper.classifyStaticImage(uri, requireContext().contentResolver)
    }

    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        binding.progressIndicator.visibility = View.GONE

        if (results.isNullOrEmpty()) {
            showToast(getString(R.string.image_classifier_failed))
            return
        }

        val topResult = results[0].categories.maxByOrNull { it.score }
        val detectedResult = topResult?.label ?: getString(R.string.image_classifier_failed)
        val confidenceScore = topResult?.score ?: -1f

        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
            putExtra(ResultActivity.EXTRA_RESULT, detectedResult)
            putExtra(ResultActivity.EXTRA_CONFIDENCE_SCORE, confidenceScore)
        }
        startActivity(intent)
    }

    override fun onError(error: String) {
        binding.progressIndicator.visibility = View.GONE
        showToast(error)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_IMAGE_URI, currentImageUri)
        val classification = viewModel.classificationResult.value
        if (classification != null) {
            outState.putString(KEY_CLASSIFICATION_RESULT, classification.first)
            outState.putFloat(KEY_CLASSIFICATION_CONFIDENCE, classification.second)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
        const val KEY_CLASSIFICATION_RESULT = "KEY_CLASSIFICATION_RESULT"
        const val KEY_CLASSIFICATION_CONFIDENCE = "KEY_CLASSIFICATION_CONFIDENCE"
    }
}
