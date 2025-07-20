package com.example.prak7

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DatabaseHelper(var context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "UserDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_ACCOUNT = "Account"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_LEVEL = "level"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE \$TABLE_ACCOUNT (" +
                "\$COLUMN_EMAIL TEXT PRIMARY KEY," +
                "\$COLUMN_NAME TEXT," +
                "\$COLUMN_LEVEL TEXT," +
                "\$COLUMN_PASSWORD TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS \$TABLE_ACCOUNT")
        onCreate(db)
    }

    fun addAccount(email: String, name: String, level: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_LEVEL, level)
        values.put(COLUMN_PASSWORD, password)

        val result = db.insert(TABLE_ACCOUNT, null, values)
        db.close()

        if (result == -1L) {
            Toast.makeText(context, "Register Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Register Success, please login using your new account", Toast.LENGTH_SHORT).show()
        }
        return result
    }

    fun checkData(email: String): String? {
        val db = this.readableDatabase
        val query = "SELECT \$COLUMN_EMAIL FROM \$TABLE_ACCOUNT WHERE \$COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        var result: String? = null
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        }
        cursor.close()
        db.close()
        return result
    }

    fun checkLogin(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM \$TABLE_ACCOUNT WHERE \$COLUMN_EMAIL = ? AND \$COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))
        val success = cursor.count > 0
        cursor.close()
        db.close()
        return success
    }
}