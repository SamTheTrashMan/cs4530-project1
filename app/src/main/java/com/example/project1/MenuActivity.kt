package com.example.project1

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class MenuActivity : AppCompatActivity() {
    private var picturePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        picturePath = intent.getStringExtra("picturePath")
        if (picturePath != null) {
            val thumbnail = BitmapFactory.decodeFile(picturePath)
            (findViewById<View>(R.id.imageViewPicture) as ImageView).setImageBitmap(thumbnail)
        }
    }
}