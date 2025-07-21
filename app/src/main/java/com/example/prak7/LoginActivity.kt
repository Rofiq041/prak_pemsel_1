package com.example.prak7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.text.InputType
import android.widget.ImageView
import com.example.prak7.DatabaseHelper

class LoginActivity : AppCompatActivity() {

    // Deklarasi view
    private lateinit var mViewUser: EditText
    private lateinit var mViewPassword: EditText
    private lateinit var mBtnLogin: Button
    private lateinit var mCheckRemember: CheckBox
    private lateinit var mTextRegister: TextView
    // private lateinit var mTogglePassword: ImageView // Dikomentari karena ID tidak ditemukan di layout

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek apakah user sudah login sebelumnya
        // Jika ya, langsung ke MainActivity
        if (Preferences.getLoggedInStatus(this)) {
            navigateToMainActivity()
            return
        }

        setContentView(R.layout.activity_login)

        // Inisialisasi view
        initializeViews()

        // Setup default user untuk testing (hanya jika belum ada user terdaftar)
        // setupDefaultUserForTesting() // Dikomentari karena sudah tidak relevan

        // Load remember me data
        loadRememberMeData()

        // Setup event listeners
        setupEventListeners()
    }

    private fun initializeViews() {
        mViewUser = findViewById(R.id.usernameInput)
        mViewPassword = findViewById(R.id.passwordInput)
        mBtnLogin = findViewById(R.id.loginButton)
        mCheckRemember = findViewById(R.id.rememberMeCheckbox)
        mTextRegister = findViewById(R.id.registerText)
        // mTogglePassword = findViewById(R.id.imageViewTogglePassword) // Dikomentari karena ID tidak ditemukan di layout
    }

    private fun loadRememberMeData() {
        // Jika remember me aktif, isi field username
        if (Preferences.getRememberMe(this)) {
            mViewUser.setText(Preferences.getLoggedInUser(this) ?: "") // Menggunakan getLoggedInUser
            mCheckRemember.isChecked = true
        }
    }

    private fun setupEventListeners() {
        // Event listener untuk tombol login
        mBtnLogin.setOnClickListener {
            performLogin()
        }

        // Event listener untuk register link
        mTextRegister.setOnClickListener {
            navigateToRegisterActivity()
        }

        // Event listener untuk toggle password visibility
        // mTogglePassword.setOnClickListener { // Dikomentari karena ID tidak ditemukan di layout
        //     togglePasswordVisibility()
        // }

        // Event listener untuk remember me checkbox
        mCheckRemember.setOnCheckedChangeListener { _, isChecked ->
            Preferences.setRememberMe(this, isChecked)
        }
    }

    private fun performLogin() {
        val username = mViewUser.text.toString().trim()
        val password = mViewPassword.text.toString()

        // Validasi input
        if (!validateInput(username, password)) {
            return
        }

        val databaseHelper = DatabaseHelper(this)
        val loginSuccess = databaseHelper.loginUser(username, password)

        if (loginSuccess) {
            // Simpan remember me preference
            Preferences.setRememberMe(this, mCheckRemember.isChecked)
            Preferences.setLoggedInUser(this, username)
            Preferences.setLoggedInStatus(this, true)

            showToast("Login berhasil!")
            navigateToMainActivity()
        } else {
            showToast("Username atau Password salah")
            // Clear password field untuk keamanan
            mViewPassword.setText("")
            mViewPassword.requestFocus()
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        // Validasi username
        if (username.isEmpty()) {
            mViewUser.error = "Username tidak boleh kosong"
            mViewUser.requestFocus()
            return false
        }

        // Validasi format email (optional)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            mViewUser.error = "Format email tidak valid"
            mViewUser.requestFocus()
            return false
        }

        // Validasi password
        if (password.isEmpty()) {
            mViewPassword.error = "Password tidak boleh kosong"
            mViewPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            mViewPassword.error = "Password minimal 6 karakter"
            mViewPassword.requestFocus()
            return false
        }

        return true
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            mViewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            // mTogglePassword.setImageResource(android.R.drawable.ic_partial_secure) // Dikomentari karena ID tidak ditemukan di layout
        } else {
            // Show password
            mViewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            // mTogglePassword.setImageResource(android.R.drawable.ic_secure) // Dikomentari karena ID tidak ditemukan di layout
        }

        // Move cursor to end
        mViewPassword.setSelection(mViewPassword.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh remember me data saat activity resume
        loadRememberMeData()
    }

    // Optional: Handle back button
    override fun onBackPressed() {
        // Jika user menekan back, keluar dari aplikasi
        finishAffinity()
    }
}