package com.example.testing

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.testing.data.ScoreDB
import kotlin.random.Random.Default.nextInt

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = ScoreDB(this@MainActivity)
        val user = if (intent.hasExtra("user"))
                intent.getStringExtra("user").toString()
        else ""

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Guess the Number")

        builder.setPositiveButton("OK"){ _: DialogInterface, _: Int ->}

        var tries = 0

        var guessNumber = nextInt(0, 20)
        val guessButton = findViewById<Button>(R.id.button)
        val logOutButton = findViewById<Button>(R.id.button3)
        val newgameButton = findViewById<Button>(R.id.button2)
        val highScoreButton = findViewById<Button>(R.id.highScores)

        newgameButton.isClickable = false
        newgameButton.isEnabled = false

        val attemptsText = findViewById<TextView>(R.id.textView3)
        attemptsText.text = (10 - tries).toString()

        val scoreText = findViewById<TextView>(R.id.textView5)
        scoreText.text = getScore(user).toString()

        val highScoreText = findViewById<TextView>(R.id.textView10)
        highScoreText.text = getHighScore(db, user).toString()

        val textBox = findViewById<EditText>(R.id.editTextNumber)


        guessButton.setOnClickListener {
            textBox.hideKeyboard()
            textBox.isFocusableInTouchMode = false
            textBox.isFocusable = false
            textBox.isFocusableInTouchMode = true
            textBox.isFocusable = true
            val string = textBox.text.toString()
            if (string != "") {
                val number = string.toInt()
                if (number <= 20) {
                    tries++
                    if (tries > 10) {
                        tries = 0
                        setFinalScore(user, getScore(user), db)
                        builder.setMessage("You lose! Final score: " + getScore(user).toString()).create().show()
                        setScore(0, user)

                        highScoreText.text = getHighScore(db, user).toString()
                        guessButton.isClickable = false
                        guessButton.isEnabled = false
                        newgameButton.isClickable = false
                        newgameButton.isEnabled = false
                        textBox.isEnabled = false
                        textBox.isClickable = false
                        guessNumber = nextInt(0, 20)
                    } else if (number > guessNumber) {
                        Toast.makeText(applicationContext, "Too high", Toast.LENGTH_SHORT).show()
                    } else if (number < guessNumber) {
                        Toast.makeText(applicationContext, "Too low", Toast.LENGTH_SHORT).show()
                    } else {
                        newgameButton.isClickable = true
                        newgameButton.isEnabled = true
                        guessButton.isClickable = false
                        guessButton.isEnabled = false
                        textBox.isEnabled = false
                        textBox.isClickable = false
                        if (tries == 1) {
                            addScore(5, user)
                            builder.setMessage("You guessed it in 1, +5 points!").create().show()
                        } else if (tries == 2 || tries == 3 || tries == 4) {
                            addScore(3, user)
                            builder.setMessage("You guessed it in $tries, +3 points!").create().show()
                        }else if (tries == 5 || tries == 6) {
                            addScore(2, user)
                            builder.setMessage("You guessed it in $tries, +2 points!").create().show()
                        } else {
                            addScore(1, user)
                            builder.setMessage("You guessed it in $tries, +1 point!").create().show()
                        }
                    }
                    if (tries <= 10) attemptsText.text = (10 - tries).toString()
                } else {
                    Toast.makeText(applicationContext, "Out of bounds", Toast.LENGTH_SHORT).show()
                }
            }
            scoreText.text = getScore(user).toString()
        }

        logOutButton.setOnClickListener {

            finish()
        }

        newgameButton.setOnClickListener {
            newgameButton.isClickable = false
            newgameButton.isEnabled = false
            guessButton.isClickable = true
            guessButton.isEnabled = true
            tries = 0
            attemptsText.text = (10 - tries).toString()
            guessNumber = nextInt(0, 20)
            scoreText.text = getScore(user).toString()
            textBox.text.clear()
            textBox.isEnabled = true
            textBox.isClickable = true
        }
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun addScore(score : Int, user : String) {
        var shared = getScore(user)
        shared += score
        setScore(shared, user)
    }

    private fun getScore(user : String): Int {
        val sharedScore = this.getSharedPreferences("com.example.myapplication.shared",0)
        return if (sharedScore.contains(user))
            sharedScore.getInt(user, 0)
        else 0
    }

    private fun setScore(score : Int, user : String) {
        val sharedScore = this.getSharedPreferences("com.example.myapplication.shared", 0)
        val edit = sharedScore.edit()
        edit.putInt(user, score)
        edit.apply()
    }

    private fun getHighScore(db : ScoreDB, user : String): Int {
        return if (db.hasScore(user))
            db.getHighScore(user)
        else 0
    }

    private fun setFinalScore(username : String, highScore : Int, db : ScoreDB) {
        db.addScore(username, highScore)
    }
}