package com.example.prak7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    // Deklarasi view
    private lateinit var mTextViewNama: TextView
    private lateinit var mButtonLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek apakah user sudah login
        // Jika belum, redirect ke LoginActivity
        if (!Preferences.getLoggedInStatus(this)) {
            navigateToLoginActivity()
            return
        }

        setContentView(R.layout.activity_main)

        // Inisialisasi view
        initializeViews()

        // Setup tampilan awal
        setupInitialDisplay()

        // Setup event listeners
        setupEventListeners()
    }

    private fun initializeViews() {
        mTextViewNama = findViewById(R.id.textViewNama)
        mButtonLogout = findViewById(R.id.buttonLogout)
    }

    private fun setupInitialDisplay() {
        // Ambil username yang sedang login dan tampilkan
        val currentUser = Preferences.getLoggedInUser(this)

        if (currentUser != null && currentUser.isNotEmpty()) {
            mTextViewNama.text = "Hello, $currentUser!"
        } else {
            // Jika tidak ada user yang login, kembali ke LoginActivity
            mTextViewNama.text = "Hello, User!"
            navigateToLoginActivity()
        }

        // Tampilkan informasi tambahan (optional)
        showWelcomeMessage()
    }

    private fun setupEventListeners() {
        // Event listener untuk tombol logout
        mButtonLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showWelcomeMessage() {
        val currentUser = Preferences.getLoggedInUser(this)
        val lastLoginTime = Preferences.getLastLoginTime(this)

        if (currentUser != null && lastLoginTime > 0) {
            val loginTime = java.text.SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                java.util.Locale.getDefault()
            ).format(java.util.Date(lastLoginTime))

            Toast.makeText(
                this,
                "Selamat datang kembali, $currentUser!\nLogin terakhir: $loginTime",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Logout")
        builder.setMessage("Apakah Anda yakin ingin logout?")

        builder.setPositiveButton("Ya") { dialog, _ ->
            performLogout()
            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun performLogout() {
        // Gunakan fungsi logout dari Preferences
        Preferences.logoutUser(this)

        // Tampilkan pesan logout
        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()

        // Kembali ke LoginActivity
        navigateToLoginActivity()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()

        // Cek lagi status login saat activity resume
        if (!Preferences.getLoggedInStatus(this)) {
            navigateToLoginActivity()
            return
        }

        // Refresh tampilan user
        val currentUser = Preferences.getLoggedInUser(this)
        if (currentUser != null && currentUser.isNotEmpty()) {
            mTextViewNama.text = "Hello, $currentUser!"
        }
    }

    // Handle back button - tampilkan dialog konfirmasi keluar
    override fun onBackPressed() {
        showExitConfirmationDialog()
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Keluar Aplikasi")
        builder.setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")

        builder.setPositiveButton("Ya") { dialog, _ ->
            finishAffinity() // Keluar dari aplikasi
            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    // Optional: Menu untuk pengaturan tambahan
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                showUserProfile()
                true
            }
            R.id.action_settings -> {
                // Navigasi ke SettingsActivity (jika ada)
                Toast.makeText(this, "Settings belum tersedia", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_about -> {
                showAboutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showUserProfile() {
        val currentUser = Preferences.getLoggedInUser(this)
        val lastLoginTime = Preferences.getLastLoginTime(this)
        val rememberMe = Preferences.getRememberMe(this)

        val loginTimeStr = if (lastLoginTime > 0) {
            java.text.SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss",
                java.util.Locale.getDefault()
            ).format(java.util.Date(lastLoginTime))
        } else {
            "Tidak tersedia"
        }

        val message = """
            Username: $currentUser
            Login Terakhir: $loginTimeStr
            Remember Me: ${if (rememberMe) "Aktif" else "Tidak Aktif"}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Profil User")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Tentang Aplikasi")
            .setMessage("Aplikasi Login Demo\nVersi 1.0\nDibuat untuk Praktikum Android")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}