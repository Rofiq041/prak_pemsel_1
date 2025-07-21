package com.example.prak7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val welcomeText: TextView = findViewById(R.id.welcomeText)
        val loggedInUser = Preferences.getLoggedInUser(applicationContext)
        welcomeText.text = "Selamat Datang! $loggedInUser"

        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            Preferences.clearLoggedInUser(applicationContext)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show()
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_makanan -> selectedFragment = FragmentMakanan()
                R.id.nav_transaksi -> selectedFragment = FragmentTransaction()
                R.id.nav_api -> selectedFragment = ApiFragment()
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment).commit()
            }
            true
        }

        // Set default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.nav_makanan
        }
    }
}