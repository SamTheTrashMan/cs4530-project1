package com.example.project1

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var fullName: String? = null
    private var cityCountry: String? = ""
    private var weight: String? = ""
    private var height: String? = ""
    private var age: String?= ""
    private var activityLevel: String? = ""
    private var sex: String? = ""

    private var picturePath: String? = ""
    private var picture: Bitmap? = null

    private var cameraButton: Button? = null
    private var signupButton: Button? = null
    private var fullNameEt: EditText? = null
    private var ageEt: Spinner? = null
    private var cityCountryEt: EditText? = null
    private var weightEt: Spinner? = null
    private var heightEt: Spinner? = null
    private var activitySpinner: Spinner? = null
    private var sexSpinner: Spinner? = null

    private var menuActivityIntent: Intent? = null
    private var drawerActivityIntent: Intent? = null

    private var inCamera: Boolean = false

    private val mAppViewModel: AppViewModel by viewModels {
        AppViewModelFactory((application as AppApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set cameraButton and signupButton onClicks
        signupButton = findViewById(R.id.buttonSignUp)
        cameraButton = findViewById(R.id.buttonPicture)
        fullNameEt = findViewById(R.id.editTextFullName)
        ageEt = findViewById(R.id.spinnerAge)
        cityCountryEt = findViewById(R.id.editTextCityCountry)
        weightEt = findViewById(R.id.spinnerWeight)
        heightEt = findViewById(R.id.spinnerHeight)
        activitySpinner = findViewById(R.id.spinnerActivity)
        sexSpinner = findViewById(R.id.spinnerSex)

        val entries = ArrayList<String>()
        entries.add("Select Age")
        for (i in 1..65) {
            entries.add(i.toString())
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item, entries
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ageEt!!.adapter = adapter

        // Height
        val heightEntries = ArrayList<String>()
        heightEntries.add("Select Height")
        for (i in 1..113) {
            heightEntries.add(i.toString())
        }
        val heightAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            heightEntries
        )
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        heightEt!!.adapter = heightAdapter

        // Weight
        val weightEntries = ArrayList<String>()
        weightEntries.add("Select Weight")
        for (i in 1..500) {
            weightEntries.add(i.toString())
        }
        val weightAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            weightEntries
        )
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        weightEt!!.adapter = weightAdapter

        if (savedInstanceState != null) {
            fullNameEt!!.setText(savedInstanceState.getString("fullName"))
            cityCountryEt!!.setText(savedInstanceState.getString("cityCountry"))
            ageEt!!.setSelection(savedInstanceState.getInt("age"))
            weightEt!!.setSelection(savedInstanceState.getInt("weight"))
            heightEt!!.setSelection(savedInstanceState.getInt("height"))
            sexSpinner!!.setSelection(savedInstanceState.getInt("sex"))
            activitySpinner!!.setSelection(savedInstanceState.getInt("activity"))

        }

        menuActivityIntent = Intent(this, MenuActivity::class.java)
        drawerActivityIntent = Intent(this, DrawerActivity::class.java)

        signupButton!!.setOnClickListener(this)
        cameraButton!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        if(!inCamera)
        {
            fullNameEt!!.setText("")
            ageEt!!.setSelection(0)
            cityCountryEt!!.setText("")
            weightEt!!.setSelection(0)
            heightEt!!.setSelection(0)
            activitySpinner!!.setSelection(0)
            sexSpinner!!.setSelection(0)
        }
        else
        {
            inCamera = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("fullName", fullNameEt!!.text.toString())
        outState.putInt("age", ageEt!!.selectedItemPosition)
        outState.putString("cityCountry", cityCountryEt!!.text.toString())
        outState.putInt("weight", weightEt!!.selectedItemPosition)
        outState.putInt("height", heightEt!!.selectedItemPosition)
        outState.putInt("activity", activitySpinner!!.selectedItemPosition)
        outState.putInt("sex", sexSpinner!!.selectedItemPosition)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonPicture -> {
                inCamera = true
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
                weight = weightEt!!.selectedItem.toString()
                height = heightEt!!.selectedItem.toString()
                age = ageEt!!.selectedItem.toString()
                if (fullName.isNullOrBlank())  {
                    Toast.makeText(this@MainActivity, "Please fill fill in the name field", Toast.LENGTH_SHORT).show()
                } else {
                    mAppViewModel.setUserData(fullName!!, cityCountry!!, activityLevel!!, sex!!,
                        picturePath!!, weight!!, height!!, age!!)

                    startActivity(drawerActivityIntent)
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
