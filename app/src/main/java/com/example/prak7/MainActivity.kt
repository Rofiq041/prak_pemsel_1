package com.example.prak7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Logic for logout, for now just a Toast
            Toast.makeText(this, "Logout Clicked", Toast.LENGTH_SHORT).show()
            // Here you would typically navigate to a Login screen and clear session
        }
    }
}