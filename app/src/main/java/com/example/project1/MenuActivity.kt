package com.example.project1

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

class MenuActivity : AppCompatActivity() {
    private var picturePath: String? = null
    private var weight: Int? = null
    private var height: Int? = null
    private var activityLevel: String? = null
    private var sex: String? = null
    private var age: Int?= null
    private var BMRVal: Double?= null

    private var BMR: TextView?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        sex = intent.getStringExtra("sex")
        activityLevel = intent.getStringExtra("activityLevel")

        age = intent.getIntExtra("age", 0)
        height = intent.getIntExtra("height", 0)
        weight = intent.getIntExtra("weight", 0)

        BMR = findViewById(R.id.textViewBMR)
        BMRVal = calculateBMR()
        BMR!!.setText(BMRVal.toString())
        picturePath = intent.getStringExtra("picturePath")
        if (picturePath != null) {
            val thumbnail = BitmapFactory.decodeFile(picturePath)
            (findViewById<View>(R.id.imageViewPicture) as ImageView).setImageBitmap(thumbnail)
        }
    }

    private fun calculateBMR(): Double {
        if(sex == "Male") {
            var BMR = 66.47 + ( 6.24 * weight!!)+ ( 12.7 * height!!) - ( 6.755 * age!!)
            return BMR
        }
        var BMR = 655.1 + ( 4.35 * weight!!) + ( 4.7 * height!!) - ( 4.7 * age!!)
        return BMR
    }
}