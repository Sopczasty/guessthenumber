package com.example.testing.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.testing.data.model.LoggedInUser
import com.example.testing.data.model.Score

class ScoreDB(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME,
    null, DATABASE_VER
) {
    companion object {
        private const val DATABASE_VER = 2
        private const val DATABASE_NAME = "Scores.db"

        //Table
        private const val TABLE_NAME = "Scores"
        private const val COL_ID = "ID"
        private const val COL_USERNAME = "Username"
        private const val COL_SCORE = "Score"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            ("CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_USERNAME TEXT, $COL_SCORE INTEGER)")
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    @SuppressLint("Range")
    fun getTopScores(): List<Score> {
        val scores = ArrayList<Score>()
        val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY $COL_SCORE DESC LIMIT 10"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        var it = 1
        if (cursor.moveToFirst()) {
            do {
                scores.add(
                    Score (
                        it,
                        cursor.getString(cursor.getColumnIndex(COL_USERNAME)),
                        cursor.getInt(cursor.getColumnIndex(COL_SCORE))
                    )
                )
                it++
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return scores
    }

    fun hasScore(user: String): Boolean {
        val selectQuery =
            "SELECT * FROM $TABLE_NAME WHERE $COL_USERNAME='$user'"
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
    fun getHighScore(user: String): Int {
        val selectQuery =
            "SELECT * FROM $TABLE_NAME WHERE $COL_USERNAME='$user' ORDER BY $COL_SCORE"
        val cursor = this.writableDatabase.rawQuery(
            selectQuery, null
        )
        cursor.moveToFirst()

        val result = cursor.getInt(cursor.getColumnIndex(COL_SCORE))
        cursor.close()
        return result
    }

    fun addScore(user: String, score : Int) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_USERNAME, user)
        values.put(COL_SCORE, score)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }
}