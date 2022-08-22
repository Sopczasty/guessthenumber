package com.example.testing.data

import android.annotation.SuppressLint
import android.util.Log
import com.example.testing.data.model.Score
import java.net.URI
import java.sql.*
import kotlin.math.max
import kotlin.random.Random

// Komunikacja ze zdalną bazą danych (PostgreSQL)
class PostgresHandler {
    @SuppressLint("AuthLeak")
    private val user = "wxfzviarwyiahw"
    private val pass = "bfc8bc4221901ba151c3563f1eb9756ee25fdfb7f69759202dd8e90a51740705"
    private val uri = "jdbc:postgresql://ec2-52-212-228-71.eu-west-1.compute.amazonaws.com:5432/daqou112j6t9j8"
    private var connection: Connection? = null
    private var scores : ArrayList<Score>? = null
    private var size = 0


    fun checkCredentials(username: String, password: String) : Int {
        var result = -2
        Thread {
            try {
                Class.forName("org.postgresql.Driver")
                connection = DriverManager.getConnection(uri, user, pass)
                val query = connection!!.createStatement()
                val res = query.executeQuery("SELECT USERNAME, PASSWORD FROM VALIDATION WHERE USERNAME=\'$username\';")
                if(res.next()) {
                    result = if(username == res.getString(1) && password == res.getString(2)) 1 else 0
                } else result -1
                connection?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.apply {
            start()
            try {
                join()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun registerCredentials(username: String, password: String) {
        Thread {
            try {
                Class.forName("org.postgresql.Driver")
                connection = DriverManager.getConnection(uri, user, pass)
                val query = connection!!.createStatement()
                query.executeQuery("INSERT INTO VALIDATION (username, password) VALUES (\'$username\',\'$password\');")
                connection?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.apply {
            start()
            try {
                join()
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }

    // Konwersja krotki z bazy danych w obiekt cytatu
    private fun parseScore(score : ResultSet) {
        Log.e("PARSE", "Score to parse")
        if(scores == null) scores = ArrayList<Score>()
        scores!!.add(Score(
            scores!!.size + 1,
            score.getString(3),
            score.getInt(2)
        ))
    }

    // Uzyskiwanie ilości cytatów w bazie danych (w celach późniejszego losowania cytatu)
    private fun fetchScoresSize(query : Statement) {
        val result = query.executeQuery("SELECT COUNT(*) FROM SCORES;")
        if (result.next()) {
            size = result.getInt(1)
            println(size)
        }
    }

    // Pobieranie cytatów z bazy danych
    private fun fetchScores() {
        Thread {
            try {
                Class.forName("org.postgresql.Driver")
                connection = DriverManager.getConnection(uri, user, pass)
                if (connection != null) Log.e("CONNECTED", "Connected!!")
                val query = connection!!.createStatement()
                val res = query.executeQuery("SELECT * FROM SCORES ORDER BY SCORE DESC LIMIT 100;")
                while(res.next()) {
                    parseScore(res)
                }
                connection?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.apply {
            start()
            try {
                join()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getHighScore(username: String): Int {
        var hs = 0
        Thread {
            try {
                Class.forName("org.postgresql.Driver")
                connection = DriverManager.getConnection(uri, user, pass)
                if (connection != null) Log.e("CONNECTED", "Connected!!")
                val query = connection!!.createStatement()
                val res = query.executeQuery("SELECT SCORE FROM SCORES WHERE USERNAME=\'$username\' ORDER BY SCORE DESC LIMIT 11;")
                if(res.next()) {
                    hs = res.getInt(1)
                }
                connection?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.apply {
            start()
            try {
                join()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return hs
    }

    fun getScores() : ArrayList<Score>? {
        scores = null
        fetchScores()
        return scores
    }

    fun putScore(score: Score) {
        Thread {
            try {
                Class.forName("org.postgresql.Driver")
                connection = DriverManager.getConnection(uri, user, pass)
                val query = connection!!.createStatement()
                val res = query.executeQuery("SELECT score FROM SCORES WHERE USERNAME=\'${score.name}\';")
                if (res.next()) {
                    val mx = max(score.score, res.getInt(1))
                    query.executeQuery("UPDATE SCORES SET score=$mx WHERE USERNAME=\'${score.name}\';")
                } else {
                    query.executeQuery("INSERT INTO SCORES (score, username) VALUES (${score.score}, \'${score.name}\');")
                }
                connection?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.apply {
            start()
        }
    }
}