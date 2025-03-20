package com.dicoding.eatsight.helper

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.dicoding.eatsight.ml.FoodsModel
import org.tensorflow.lite.support.image.TensorImage

class ImageClassifierHelper(private val context: Context) {

    interface ClassifierListener {
        fun onResults(result: String)
        fun onError(error: String)
    }

    fun classifyImage(bitmap: Bitmap, listener: ClassifierListener) {
        try {
            val foodsModel = FoodsModel.newInstance(context)

            val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val tfImage = TensorImage.fromBitmap(newBitmap)

            val outputs = foodsModel.process(tfImage)
                .probabilityAsCategoryList.apply {
                    sortByDescending { it.score }
                }

            val highProbabilityOutput = outputs[0]
            listener.onResults(highProbabilityOutput.label)

            foodsModel.close()
        } catch (e: Exception) {
            Log.e(TAG, "Classification failed: ${e.message}")
            listener.onError("Classification failed: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}