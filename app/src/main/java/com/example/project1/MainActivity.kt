package com.example.project1

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var fullName: String? = null
    private var cityCountry: String? = null
    private var weight: Int? = null
    private var height: Int? = null
    private var activityLevel: String? = null
    private var sex: String? = null

    private var picturePath: String? = null
    private var picture: Bitmap? = null

    private var cameraButton: Button? = null
    private var signupButton: Button? = null
    private var fullNameEt: EditText? = null
    private var ageEt: EditText? = null
    private var cityCountryEt: EditText? = null
    private var weightEt: EditText? = null
    private var heightEt: EditText? = null
    private var activitySpinner: Spinner? = null
    private var sexSpinner: Spinner? = null


    private var menuActivityIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set cameraButton and signupButton onClicks
        signupButton = findViewById(R.id.buttonSignUp)
        cameraButton = findViewById(R.id.buttonPicture)
        fullNameEt = findViewById(R.id.editTextFullName)
        ageEt = findViewById(R.id.editTextAge)
        cityCountryEt = findViewById(R.id.editTextCityCountry)
        weightEt = findViewById(R.id.editTextWeight)
        heightEt = findViewById(R.id.editTextHeight)
        activitySpinner = findViewById(R.id.spinnerActivity)
        sexSpinner = findViewById(R.id.spinnerSex)


        menuActivityIntent = Intent(this, MenuActivity::class.java)

        signupButton!!.setOnClickListener(this)
        cameraButton!!.setOnClickListener(this)
    }
    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonPicture -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    cameraActivity.launch(cameraIntent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(this@MainActivity, ex.toString(), Toast.LENGTH_LONG).show()
                }
            }
            R.id.buttonSignUp -> {
                fullName = fullNameEt!!.text.toString()
                cityCountry = cityCountryEt!!.text.toString()
                activityLevel = activitySpinner!!.selectedItem.toString()
                sex = sexSpinner!!.selectedItem.toString()
                weight = weightEt!!.text.toString().toIntOrNull()
                height = heightEt!!.text.toString().toIntOrNull()

                if (fullName.isNullOrBlank() || cityCountry.isNullOrBlank() || activityLevel == "Select Activity Level" ||
                    picturePath.isNullOrBlank() || weight == null || height == null || sex == "Select Sex") {
                    Toast.makeText(this@MainActivity, "Please fill out all fields and take a picture", Toast.LENGTH_SHORT).show()
                } else {
                    val bundle = Bundle()
                    bundle.putString("fullName", fullName)
                    bundle.putString("cityCountry", cityCountry)
                    bundle.putString("activityLevel", activityLevel)
                    bundle.putString("sex", sex)
                    bundle.putString("picturePath", picturePath)
                    bundle.putInt("weight", weight!!)
                    bundle.putInt("height", height!!)

                    menuActivityIntent!!.putExtras(bundle)
                    startActivity(menuActivityIntent)
                }
            }
        }
    }

    private fun saveImage(finalBitmap: Bitmap?): String {
        val myDir = File("${getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/saved_images")
        myDir.mkdirs()
        val file = File(myDir, "Thumbnail_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg")
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Toast.makeText(this, "file saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK) {
            val extras = result.data!!.extras
            picture = extras!!["data"] as Bitmap?
            if (isExternalStorageWritable) {
                picturePath = saveImage(picture)
            } else {
                Toast.makeText(this, "External storage not writable.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
