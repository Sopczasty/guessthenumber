package com.example.testing.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.testing.data.model.LoggedInUser

class LoginDB(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME,
    null, DATABASE_VER
) {
    companion object {
        private const val DATABASE_VER = 2
        private const val DATABASE_NAME = "USERS.db"
        //Table
        private const val TABLE_NAME = "Users"
        private const val COL_USERNAME = "Username"
        private const val COL_PASSWORD = "Password"
        private const val COL_IS_LOGGED = "Is_Logged"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            ("CREATE TABLE $TABLE_NAME ($COL_USERNAME TEXT PRIMARY KEY, $COL_PASSWORD TEXT, $COL_IS_LOGGED INTEGER)")
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun validateUser(user: String, password: String): Boolean {
        val selectQuery =
            "SELECT * FROM $TABLE_NAME WHERE $COL_USERNAME='$user' AND $COL_PASSWORD='$password'"
        val cursor = this.writableDatabase?.rawQuery(
            selectQuery, null
        )
        return if (cursor?.moveToFirst() == true) {
            cursor.close()
            true
        } else {
            false
        }
    }

    @SuppressLint("Range")
    fun getUser(user: String, password: String): LoggedInUser {
        val selectQuery =
            "SELECT * FROM $TABLE_NAME WHERE $COL_USERNAME='$user' AND $COL_PASSWORD='$password'"
        val cursor = this.writableDatabase.rawQuery(
            selectQuery, null
        )
        cursor.moveToFirst()

        val result = LoggedInUser(
            displayName = cursor.getString(cursor.getColumnIndex(COL_USERNAME))
        )
        cursor.close()
        return result
    }

    fun addUser(user: String, password: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_USERNAME, user)
        values.put(COL_PASSWORD, password)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
}