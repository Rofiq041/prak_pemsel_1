package com.example.prak7

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prak7.DatabaseHelper

class AddMenuActivity : AppCompatActivity() {

    private lateinit var image: ImageView

    companion object {
        const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_menu)
        supportActionBar?.hide()

        image = findViewById(R.id.imageMenu)
        val textId: EditText = findViewById(R.id.menuId)
        val textName: EditText = findViewById(R.id.menuName)
        val textPrice: EditText = findViewById(R.id.menuPrice)
        val btnAddImage: Button = findViewById(R.id.buttonAddImage)
        val btnSaveMenu: Button = findViewById(R.id.buttonSaveMenu)

        btnAddImage.setOnClickListener {
            pickImageGallery()
        }

        btnSaveMenu.setOnClickListener {
            val databaseHelper = DatabaseHelper(this)

            val id = textId.text.toString().toInt()
            val name = textName.text.toString().trim()
            val price = textPrice.text.toString().toInt()
            val bitmapDrawable = image.drawable as? BitmapDrawable
            val bitmap = bitmapDrawable?.bitmap

            if (bitmap != null) {
                val menuModel = MenuModel(id, name, price, bitmap)
                databaseHelper.addMenu(menuModel)
                finish()
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            image.setImageURI(data?.data)
        }
    }
}