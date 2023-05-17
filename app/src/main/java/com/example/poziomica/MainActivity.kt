package com.example.poziomica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonPionowo = findViewById<Button>(R.id.button_pionowo)
        val buttonPoziomo = findViewById<Button>(R.id.button_poziomo)

        buttonPionowo.setOnClickListener {
            val intent = Intent(this, PionowoActivity::class.java)
            startActivity(intent)
        }

        buttonPoziomo.setOnClickListener {
            val intent = Intent(this, PoziomoActivity::class.java)
            startActivity(intent)
        }
    }
}
