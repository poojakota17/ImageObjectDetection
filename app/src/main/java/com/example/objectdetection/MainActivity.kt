package com.example.objectdetection

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify


import com.amplifyframework.core.Consumer
import com.amplifyframework.predictions.PredictionsException
import com.amplifyframework.predictions.aws.AWSPredictionsPlugin
import com.amplifyframework.predictions.models.Label
import com.amplifyframework.predictions.models.LabelType
import com.amplifyframework.predictions.models.LabelType.LABELS
import com.amplifyframework.predictions.result.IdentifyLabelsResult
import com.amplifyframework.predictions.result.IdentifyResult
import kotlinx.coroutines.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private var CAMERA_REQUEST_CODE=0
    private var IMAGE_PICK_CODE=0
    private var PERMISSION_CODE=0
     lateinit var  bitmapimage : Bitmap

    var strText: String =""
    var flag : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSPredictionsPlugin())
            Amplify.configure(applicationContext)
            Log.i("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }
        setContentView(R.layout.activity_main)
    }


    fun cameraclick(view: View) {
        CAMERA_REQUEST_CODE=200
        checkPermissionAndOpenCamera(CAMERA_REQUEST_CODE)

    }
    fun galleryclick(view: View) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            //permission denied
            val permission : Array<String> = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permission,PERMISSION_CODE)
        }else{
            pickFromImageGallery()
        }
    }else{
        pickFromImageGallery()
    }
    }

    private fun pickFromImageGallery(){
        val intent=Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent,IMAGE_PICK_CODE)
    }

    private fun takePhotoFromCamera(requestCode: Int){
        val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, requestCode)

    }
    private fun checkPermissionAndOpenCamera(requestCode:Int){
        if(ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),5)
        }else {
            takePhotoFromCamera(requestCode)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 5){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                takePhotoFromCamera(CAMERA_REQUEST_CODE)
            }
        }
        when(requestCode){
            PERMISSION_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickFromImageGallery()
                }else {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imgview=findViewById<ImageView>(R.id.imageView)
       var text :String ="puppu"
        if(resultCode == Activity.RESULT_OK){
            if (requestCode==200 && data!=null){
                bitmapimage=data.extras?.get("data") as Bitmap
                imgview.setImageBitmap(data.extras?.get("data") as Bitmap)
            } else if (requestCode == IMAGE_PICK_CODE){
                imgview.setImageURI(data?.data)
                bitmapimage=  (imgview.drawable as BitmapDrawable).bitmap
            }
          //  detectLabels(bitmapimage)
        }
    }

    fun findobjects(view: View) {
        detectLabels(bitmapimage)
        while (!flag){
            Thread.sleep(2_000)
        }
        flag=false
        basicAlert(strText)
    }


   fun detectLabels(image: Bitmap) {

      Amplify.Predictions.identify(
                    LabelType.LABELS,
                    image,
                    Consumer { result: IdentifyResult ->
                        val identifyResult = result as IdentifyLabelsResult
                        strText=""
                        for (res in identifyResult.labels) {
                            strText = strText + res.name + "\n"
                            Log.i("MyAmplifyApp", res.name)
                        }
                        flag=true
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

    fun basicAlert(str: String){
        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            Toast.makeText(applicationContext,
                "OK", Toast.LENGTH_SHORT).show()
        }

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("Objects in Image")
            setMessage(str)
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))

            show()
        }


    }
}