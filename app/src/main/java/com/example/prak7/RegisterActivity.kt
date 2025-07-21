package com.example.prak7

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prak7.R
import com.example.prak7.LoginActivity
import com.example.prak7.DatabaseHelper

class RegisterActivity : AppCompatActivity() {

    private lateinit var txtEmail: EditText
    private lateinit var txtName: EditText
    // private lateinit var txtLevel: EditText // Dikomentari karena tidak ada di layout
    private lateinit var txtPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var mLoginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txtEmail = findViewById(R.id.usernameInput)
        txtName = findViewById(R.id.fullNameInput)
        // txtLevel = findViewById(R.id.registerLevel) // Dikomentari karena tidak ada di layout
        txtPassword = findViewById(R.id.passwordInput)
        btnRegister = findViewById(R.id.registerButton)
        mLoginText = findViewById(R.id.loginText)

        mLoginText.setOnClickListener { view: View ->
            val intentLogin = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intentLogin)
            finish()
        }

        btnRegister.setOnClickListener { view: View ->
            val databaseHelper = DatabaseHelper(this)

            val email = txtEmail.text.toString().trim()
            val name = txtName.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = databaseHelper.checkData(email)

            if (data != null && data.count == 0) {
                val result = databaseHelper.addAccount(email, name, password)
                if (result != -1L) {
                    val intentLogin = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intentLogin)
                    finish()
                }
            } else {
                Toast.makeText(this@RegisterActivity, "Register failed. Your email already registered", Toast.LENGTH_SHORT).show()
            }
            data?.close()
        }
    }
}