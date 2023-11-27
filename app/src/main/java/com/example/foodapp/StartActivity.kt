package com.example.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val btn_next:Button=findViewById(R.id.nextButton)
        btn_next.setOnClickListener {
            val intent = Intent(this@StartActivity,LoginActivity::class.java)
            startActivity(intent)
        }
    }
}