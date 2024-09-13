package com.example.practice.draw

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.practice.R
import com.example.practice.draw.motionlayout.MotionLayoutKeyPostionDemoActivity
import com.example.practice.draw.motionlayout.MotionLayoutWzrActivity
import com.example.practice.draw.motionlayout.MotionLayoutYouTuBeActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.text1).setOnClickListener {
            startActivity(Intent(this, MotionLayoutWzrActivity::class.java))
        }
        findViewById<TextView>(R.id.text2).setOnClickListener {
            startActivity(Intent(this, MotionLayoutKeyPostionDemoActivity::class.java))
        }

        findViewById<TextView>(R.id.text3).setOnClickListener {
            startActivity(Intent(this, MotionLayoutYouTuBeActivity::class.java))
        }
    }
}