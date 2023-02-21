package com.example.project1

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity(), View.OnClickListener {
    //ui elements
    private var cameraButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set cameraButton to buttonPicture
        cameraButton = findViewById(R.id.buttonPicture)
        cameraButton!!.setOnClickListener(this)
    }
    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonPicture -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraActivity.launch(cameraIntent)
            }
        }
    }
    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK) {
            //TODO store picture and figure out how to display in corner
        }
    }
}