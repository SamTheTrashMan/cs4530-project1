package com.example.project1

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MenuActivity : AppCompatActivity(), View.OnClickListener {
    private var picturePath: String? = null
    private var weight: Int? = null
    private var height: Int? = null
    private var activityLevel: String? = null
    private var sex: String? = null
    private var age: Int?= null
    private var BMRVal: Double?= null
    private var calTarget: Double?= null
    private var cityCountry: String?= null


    private var BMR: TextView?= null
    private var mapsButton: Button?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        sex = intent.getStringExtra("sex")
        activityLevel = intent.getStringExtra("activityLevel")

        age = intent.getStringExtra("age")!!.toIntOrNull()
        height = intent.getStringExtra("height")!!.toIntOrNull()
        weight = intent.getStringExtra("weight")!!.toIntOrNull()
        cityCountry = intent.getStringExtra("cityCountry")

        BMR = findViewById(R.id.textViewBMR)
        if (age == null || height == null || weight == null || sex == "Select Sex") {
            BMRVal = 0.0
        } else {
            BMRVal = calculateBMR()
            if (activityLevel == "Sedentary")
            {
                calTarget = BMRVal!! * 1.2
            }
            else if(activityLevel == "Light Exercise")
            {
                calTarget = BMRVal!! * 1.375
            }
            else if (activityLevel == "Moderate Exercise")
            {
                calTarget = BMRVal!! * 1.55
            }
            else if (activityLevel == "Heavy Exercise")
            {
                calTarget = BMRVal!! * 1.725
            }
            else if (activityLevel == "Athlete")
            {
                calTarget = BMRVal!! * 1.9
            }
            else
            {
                calTarget = 0.0
            }
        }

        BMR!!.text = BMRVal.toString()
        picturePath = intent.getStringExtra("picturePath")
        if (picturePath != null) {
            val thumbnail = BitmapFactory.decodeFile(picturePath)
            (findViewById<View>(R.id.imageViewPicture) as ImageView).setImageBitmap(thumbnail)
        }

        mapsButton = findViewById(R.id.mapsButton)
        mapsButton!!.setOnClickListener(this)
    }

    private fun calculateBMR(): Double {
        if(sex == "Male") {
            return 66.47 + ( 6.24 * weight!!)+ ( 12.7 * height!!) - ( 6.755 * age!!)
        }
        return 655.1 + ( 4.35 * weight!!) + ( 4.7 * height!!) - ( 4.7 * age!!)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mapsButton -> {

                if (cityCountry.isNullOrBlank()) {
                    Toast.makeText(this, "No Location to Search", Toast.LENGTH_SHORT).show()
                } else {
                    //We have to grab the search term and construct a URI object from it.
                    //We'll hardcode WEB's location here
                    val searchUri = Uri.parse("geo:0,0?q=$cityCountry" + " Hikes")

                    //Create the implicit intent
                    val mapIntent = Intent(Intent.ACTION_VIEW, searchUri)

                    //If there's an activity associated with this intent, launch it
                    try{
                        startActivity(mapIntent)
                    }catch(ex: ActivityNotFoundException){
                        //handle errors here
                    }
                }
            }
        }
    }
}