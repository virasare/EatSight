package com.dicoding.eatsight.ui.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.eatsight.databinding.FragmentHomeBinding
import com.dicoding.eatsight.helper.ImageClassifierHelper
import com.dicoding.eatsight.ui.result.ResultActivity

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var classifierHelper: ImageClassifierHelper
    private lateinit var homeViewModel: HomeViewModel

    private var imageUri: Uri? = null
    private var selectedBitmap: Bitmap? = null
    private var classificationResult: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        classifierHelper = ImageClassifierHelper(requireContext())

        homeViewModel.currentImage.observe(viewLifecycleOwner) { bitmap ->
            bitmap?.let {
                binding.imageView.setImageBitmap(it)
            }
        }

        homeViewModel.progressVisibility.observe(viewLifecycleOwner) { visibility ->
            binding.progressIndicator.visibility = visibility
        }

        binding.cameraButton.setOnClickListener {
            takePicturePreview.launch()
        }

        binding.galleryButton.setOnClickListener {
            openGallery()
        }

        binding.analyzeButton.setOnClickListener {
            selectedBitmap?.let {
                homeViewModel.setProgressVisibility(View.VISIBLE)
                classifyImage(it)
            } ?: Toast.makeText(requireContext(), "Pilih gambar terlebih dahulu!", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                imageUri = uri
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    selectedBitmap = bitmap

                    homeViewModel.setCurrentImage(bitmap)
                    homeViewModel.setProgressVisibility(View.GONE)
                } catch (e: Exception) {
                    Log.e("TAG", "Error loading image: ${e.message}")
                }
            }
        }
    }

    private fun classifyImage(bitmap: Bitmap) {
        classifierHelper.classifyImage(bitmap, object : ImageClassifierHelper.ClassifierListener {
            override fun onResults(result: String) {
                classificationResult = result
                homeViewModel.setProgressVisibility(View.GONE)
                navigateToResultActivity()
            }

            override fun onError(error: String) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_SHORT).show()
                homeViewModel.setProgressVisibility(View.GONE)
            }
        })
    }

    private fun navigateToResultActivity() {
        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_RESULT, classificationResult)
            selectedBitmap?.let {
                val uri = getImageUri(it)
                putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
            }
        }
        startActivity(intent)
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, bitmap, "classified_image", null)
        return Uri.parse(path)
    }

    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            homeViewModel.setCurrentImage(bitmap)
            selectedBitmap = bitmap
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 100
    }
}