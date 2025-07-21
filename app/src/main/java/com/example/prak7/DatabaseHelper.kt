package com.example.prak7

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import android.util.Base64
import java.security.MessageDigest
import com.example.prak7.MenuModel

class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Pizza.db"

        private const val TABLE_MENU = "menu"
        private const val COLUMN_ID_MENU = "idMenu"
        private const val COLUMN_NAMA_MENU = "menuName"
        private const val COLUMN_PRICE_MENU = "price"
        private const val COLUMN_IMAGE = "photo"

        private const val TABLE_ACCOUNT = "account"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PASSWORD = "password"

        private const val CREATE_MENU_TABLE = ("CREATE TABLE " + TABLE_MENU + "("
                + COLUMN_ID_MENU + " INTEGER PRIMARY KEY, " + COLUMN_NAMA_MENU + " TEXT, "
                + COLUMN_PRICE_MENU + " INTEGER, " + COLUMN_IMAGE + " BLOB)")

        private const val CREATE_ACCOUNT_TABLE = ("CREATE TABLE " + TABLE_ACCOUNT + "("
                + COLUMN_EMAIL + " TEXT PRIMARY KEY, " + COLUMN_NAME + " TEXT, "
                + COLUMN_PASSWORD + " TEXT)")

        private const val DROP_MENU_TABLE = "DROP TABLE IF EXISTS $TABLE_MENU"
        private const val DROP_ACCOUNT_TABLE = "DROP TABLE IF EXISTS $TABLE_ACCOUNT"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_MENU_TABLE)
        db?.execSQL(CREATE_ACCOUNT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_MENU_TABLE)
        db?.execSQL(DROP_ACCOUNT_TABLE)
        onCreate(db)
    }

    fun addMenu(menu: MenuModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ID_MENU, menu.id)
        values.put(COLUMN_NAMA_MENU, menu.name)
        values.put(COLUMN_PRICE_MENU, menu.price)

        val byteOutputStream = ByteArrayOutputStream()
        menu.image.compress(Bitmap.CompressFormat.JPEG, 100, byteOutputStream)
        val imageInByte = byteOutputStream.toByteArray()
        values.put(COLUMN_IMAGE, imageInByte)

        val result = db.insert(TABLE_MENU, null, values)
        if (result == (-1).toLong()) {
            Toast.makeText(context, "Add menu Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Add menu Success", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    fun showMenu(): ArrayList<MenuModel> {
        val listModel = ArrayList<MenuModel>()
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_MENU, null)
        } catch (se: SQLiteException) {
            db.execSQL(CREATE_MENU_TABLE)
            return ArrayList()
        }

        var id: Int
        var name: String
        var price: Int
        var imageArray: ByteArray
        var imageBmp: Bitmap

        if (cursor!!.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_MENU))
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_MENU))
                price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE_MENU))
                imageArray = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))

                val byteInputStream = ByteArrayInputStream(imageArray)
                imageBmp = BitmapFactory.decodeStream(byteInputStream)

                val model = MenuModel(id = id, name = name, price = price, image = imageBmp)
                listModel.add(model)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listModel
    }

    fun addAccount(email: String, name: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_PASSWORD, hashPassword(password)) // Hash password before storing
        val result = db.insert(TABLE_ACCOUNT, null, values)
        db.close()
        return result
    }

    fun checkData(email: String): Cursor? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_ACCOUNT WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        return cursor
    }

    fun loginUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_ACCOUNT WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val hashedPassword = hashPassword(password)
        val cursor = db.rawQuery(query, arrayOf(email, hashedPassword))
        val success = cursor.count > 0
        cursor.close()
        return success
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return Base64.encodeToString(hashBytes, Base64.DEFAULT)
    }
}