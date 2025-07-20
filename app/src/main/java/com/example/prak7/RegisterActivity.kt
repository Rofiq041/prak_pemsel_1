package com.example.prak7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {

    private lateinit var txtEmail: EditText
    private lateinit var txtName: EditText
    private lateinit var txtLevel: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txtEmail = findViewById(R.id.registerEmail)
        txtName = findViewById(R.id.registerPersonName)
        txtLevel = findViewById(R.id.registerLevel)
        txtPassword = findViewById(R.id.registerPassword)
        btnRegister = findViewById(R.id.buttonRegisterAccount)

        btnRegister.setOnClickListener {
            val databaseHelper = DatabaseHelper(this)

            val email = txtEmail.text.toString().trim()
            val name = txtName.text.toString().trim()
            val level = txtLevel.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            if (email.isEmpty() || name.isEmpty() || level.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = databaseHelper.checkData(email)

            if (data == null) {
                val result = databaseHelper.addAccount(email, name, level, password)
                if (result != -1L) {
                    val intentLogin = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intentLogin)
                    finish()
                }
            } else {
                Toast.makeText(this@RegisterActivity, "Register failed. Your email already registered", Toast.LENGTH_SHORT).show()
            }
        }
    }
}