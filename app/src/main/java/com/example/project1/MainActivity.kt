package com.example.project1

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.math.sign

class MainActivity : AppCompatActivity(), View.OnClickListener {
    //vars
    private var fullName: String? = null
    private var weight: Int? = null
    private var height: Int? = null //thinking about just storing inches

    //ui elements
    private var cameraButton: Button? = null
    private var signupButton: Button? = null
    private var fullNameEt: EditText? = null
    private var ageEt: EditText? = null
    private var cityCountryEt: EditText? = null
    private var weightEt: EditText? = null
    private var heightEt: EditText? = null
    private var activitySpinner: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set cameraButton and signupButton onClicks
        signupButton = findViewById(R.id.buttonSignUp)
        cameraButton = findViewById(R.id.buttonPicture)

        signupButton!!.setOnClickListener(this)
        cameraButton!!.setOnClickListener(this)
    }
    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonPicture -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraActivity.launch(cameraIntent)
            }
            R.id.buttonSignUp -> {
                //TODO
            }
        }
    }
    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK) {
            //TODO store picture and figure out how to display in corner
        }
    }
}