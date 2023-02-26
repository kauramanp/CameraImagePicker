package com.amanpreet.imagepicker

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.amanpreet.imagepicker.BuildConfig.APPLICATION_ID
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
    private var imageFile: File? = null
    private var image: ImageView? = null
    private val TAG = "MainActivity"
    val takeImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
        image?.setImageURI(Uri.fromFile(imageFile))

    }
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                createImageFile()
            } else {
                var alertDialog = AlertDialog.Builder(this)
                alertDialog.apply {
                    setTitle("Permission required")
                    setMessage("Permission required to run the app")
                    setCancelable(false)
                    setPositiveButton("Ok") { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                }
                alertDialog.show()

            }
        }

    fun createImageFile() {
        val timeStamp = Calendar.getInstance().timeInMillis
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        imageFile = File.createTempFile(
            "${timeStamp}",
            ".jpg",
            storageDir
        )
        imageFile?.let {
            imageUri = FileProvider.getUriForFile(
                this@MainActivity,
                APPLICATION_ID + ".provider",
                it
            )
            takeImage.launch(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        image = findViewById(R.id.iv)
    }

    fun getImage(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }else{
            createImageFile()
        }
    }
}