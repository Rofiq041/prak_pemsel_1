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
        private const val DATABASE_VERSION = 2
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

        //tabel transaksi
        private const val TABLE_TRANS = "transaksi"
        private const val COLUMN_ID_TRANS = "idTransaksi"
        private const val COLUMN_TGL = "tanggal"
        private const val COLUMN_USER = "user"

        //tabel detail transaksi
        private const val TABLE_DET_TRANSACTION = "detailTrans"
        private const val COLUMN_ID_DET_TRX = "idDetailTrx"
        private const val COLUMN_ID_TRX = "idTransaksi"
        private const val COLUMN_ID_PESAN = "idMenu"
        private const val COLUMN_HARGA_PESAN = "harga"
        private const val COLUMN_JUMLAH = "jumlah"

        private const val CREATE_TRANSACTION_TABLE = ("CREATE TABLE " + TABLE_TRANS + "("
                + COLUMN_ID_TRANS + " INTEGER PRIMARY KEY, " + COLUMN_TGL + " TEXT, "
                + COLUMN_USER + " TEXT)")

        private const val CREATE_DET_TRANS_TABLE = ("CREATE TABLE " + TABLE_DET_TRANSACTION + "("
                + COLUMN_ID_DET_TRX + " INTEGER PRIMARY KEY, " + COLUMN_ID_TRX + " INTEGER, "
                + COLUMN_ID_PESAN + " INTEGER, " + COLUMN_HARGA_PESAN + " INTEGER, "
                + COLUMN_JUMLAH + " INTEGER)")

        private const val DROP_TRANSACTION_TABLE = "DROP TABLE IF EXISTS $TABLE_TRANS"
        private const val DROP_DET_TRANS_TABLE = "DROP TABLE IF EXISTS $TABLE_DET_TRANSACTION"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_MENU_TABLE)
        db?.execSQL(CREATE_ACCOUNT_TABLE)
        db?.execSQL(CREATE_TRANSACTION_TABLE)
        db?.execSQL(CREATE_DET_TRANS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_MENU_TABLE)
        db?.execSQL(DROP_ACCOUNT_TABLE)
        db?.execSQL(DROP_TRANSACTION_TABLE)
        db?.execSQL(DROP_DET_TRANS_TABLE)
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

    fun addTransaction(){
        val dbInsert = this.writableDatabase
        val dbSelect = this.readableDatabase
        //declare var
        var lastIdTrans = 0
        var lastIdDetail = 0
        var newIdTrans = 0
        var newIdDetail = 0
        val values = ContentValues()
        //get last idTransaksi
        val cursorTrans: Cursor = dbSelect.rawQuery(
            "SELECT * FROM $TABLE_TRANS", null)
        if (cursorTrans.moveToLast()) {
            lastIdTrans = cursorTrans.getInt(0) //to get id, 0 is the column index
        }
        val cursorDetail: Cursor = dbSelect.rawQuery(
            "SELECT * FROM $TABLE_DET_TRANSACTION", null)
        if (cursorDetail.moveToLast()) {
            lastIdDetail = cursorDetail.getInt(0) //to get id, O is the column index
        }
        //set data
        newIdTrans = lastIdTrans + 1
        val sdf= java.text.SimpleDateFormat("yyyy-MM-dd")
        val tanggal = sdf.format(java.util.Date())
        val username = Preferences.getLoggedInUser(context)
        //insert data transaksi
        values.put(COLUMN_ID_TRANS, newIdTrans)
        values.put(COLUMN_TGL, tanggal)
        values.put(COLUMN_USER, username)
        val result = dbInsert.insert(TABLE_TRANS, null, values)
        //show message
        if (result==(0).toLong()){
            Toast.makeText(context, "Add transaction Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Add transaction Success", Toast.LENGTH_SHORT).show()
        }
        newIdDetail = lastIdDetail + 1
        var i = 0
        val values2 = ContentValues()
        while(i < TransaksiAdapter.listId.count()){
            values2.put(COLUMN_ID_DET_TRX, newIdDetail)
            values2.put(COLUMN_ID_TRX, newIdTrans)
            values2.put(COLUMN_ID_PESAN, TransaksiAdapter.listId[i])
            values2.put(COLUMN_HARGA_PESAN, TransaksiAdapter.listHarga[i])
            values2.put(COLUMN_JUMLAH, TransaksiAdapter.listJumlah[i])
            val result2 = dbInsert.insert(TABLE_DET_TRANSACTION, null, values2)
            //show message
            if (result2==(0).toLong()){
                Toast.makeText(context, "Add detail Failed", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, "Add detail Success", Toast.LENGTH_SHORT).show()
            }
            newIdDetail += 1
            i+=1
        }
        dbSelect.close()
        dbInsert.close()
    }
}