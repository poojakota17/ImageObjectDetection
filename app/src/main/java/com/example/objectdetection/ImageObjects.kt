package com.example.objectdetection

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.Consumer
import com.amplifyframework.predictions.PredictionsException
import com.amplifyframework.predictions.models.Label
import com.amplifyframework.predictions.models.LabelType
import com.amplifyframework.predictions.result.IdentifyLabelsResult
import com.amplifyframework.predictions.result.IdentifyResult


class ImageObjects : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_objects)
        val byteArray = intent.getByteArrayExtra("image")
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        detectLabels(bmp)

    }
    fun detectLabels(image: Bitmap?) {
        Amplify.Predictions.identify(
            LabelType.LABELS,
            image!!,
            Consumer { result: IdentifyResult ->
                val identifyResult = result as IdentifyLabelsResult
                val label: Label = identifyResult.labels[0]
                Log.i("MyAmplifyApp", label.getName())
            },
            Consumer { error: PredictionsException? ->
                Log.e(
                    "MyAmplifyApp",
                    "Label detection failed",
                    error
                )
            }
        )
    }
}