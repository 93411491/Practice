package com.example.practice.draw

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.practice.LogcatUtil
import com.example.practice.R
import com.example.practice.draw.motionlayout.MotionLayoutKeyPostionDemoActivity
import com.example.practice.draw.motionlayout.MotionLayoutWzrActivity
import com.example.practice.draw.motionlayout.MotionLayoutYouTuBeActivity
import com.example.practice.ext.logd

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.long_click_text).setOnLongClickListener {
            "长按测试".logd("MainActivity")
            true
        }
//        findViewById<TextView>(R.id.text1).setOnClickListener {
//            startActivity(Intent(this, MotionLayoutWzrActivity::class.java))
//        }
//        findViewById<TextView>(R.id.text2).setOnClickListener {
//            startActivity(Intent(this, MotionLayoutKeyPostionDemoActivity::class.java))
//        }
//
//        findViewById<TextView>(R.id.text3).setOnClickListener {
//            startActivity(Intent(this, MotionLayoutYouTuBeActivity::class.java))
//        }
//        LogcatUtil.init(this)
//
//        findViewById<TextView>(R.id.text4).setOnClickListener{
//            LogcatUtil.startLogcat()
//        }
//
//        findViewById<TextView>(R.id.text5).setOnClickListener{
//            LogcatUtil.stopLogcat()
//        }
//
//        findViewById<TextView>(R.id.text6).setOnClickListener {
//            "打印日志".logd("MainActivity")
//        }
//
//        findViewById<TextView>(R.id.text7).setOnClickListener{
//            LogcatUtil.deleteLogFile()
//        }
//
//        findViewById<TextView>(R.id.content).setOnClickListener{
//            findViewById<TextView>(R.id.content).text = LogcatUtil.getLogcatLogs()
//        }

    }
}