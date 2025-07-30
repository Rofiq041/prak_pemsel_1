package com.example.prak7

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class AddMenuActivity : AppCompatActivity() {

    private lateinit var image: ImageView

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let {
                image.setImageURI(it)
            }
        }
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
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImage.launch(intent)
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
}