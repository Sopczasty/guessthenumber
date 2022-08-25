package com.example.testing

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.testing.data.PostgresHandler
import com.example.testing.data.ScoreDB
import com.example.testing.data.model.Score
import kotlin.properties.Delegates
import kotlin.random.Random

class GameFragment(private val user : String) : Fragment() {
    private lateinit var db : ScoreDB
    private lateinit var pgdb : PostgresHandler
    private var tries = 0
    private lateinit var builder : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Guess the Number")
        builder.setPositiveButton("OK"){ _: DialogInterface, _: Int ->}

        db = ScoreDB(requireContext())
        pgdb = (context as MainScreenActivity).getPostgresDatabase()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_game, container, false)
        var guessNumber = Random.nextInt(0, 20)
        val guessButton = view.findViewById<Button>(R.id.button)

        val attemptsText = view.findViewById<TextView>(R.id.textView3)
        attemptsText.text = (10 - tries).toString()

        val scoreText = view.findViewById<TextView>(R.id.textView5)
        scoreText.text = getScore(user).toString()

        val highScoreText = view.findViewById<TextView>(R.id.textView10)
        highScoreText.text = getHighScore(db, user).toString()

        val textBox = view.findViewById<EditText>(R.id.editTextNumber)


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
                    if (tries >= 10) {
                        tries = 0
                        setFinalScore(user, getScore(user))
                        parentFragmentManager.setFragmentResult("updateRecyclerList", bundleOf("key" to ""))
                        builder.setMessage("You lose! You should've gone for $guessNumber. Final score: " + getScore(user).toString()).create().show()
                        setScore(0, user)

                        highScoreText.text = getHighScore(db, user).toString()

                        guessNumber = Random.nextInt(1, 20)

                        textBox.text.clear()
                    } else if (number > guessNumber) {
                        Toast.makeText(requireContext(), "Too high", Toast.LENGTH_SHORT).show()
                    } else if (number < guessNumber) {
                        Toast.makeText(requireContext(), "Too low", Toast.LENGTH_SHORT).show()
                    } else {
                        guessNumber = Random.nextInt(0, 20)
                        scoreText.text = getScore(user).toString()
                        textBox.text.clear()
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
                        tries = 0
                    }
                    attemptsText.text = (10 - tries).toString()
                } else {
                    Toast.makeText(requireContext(), "Out of bounds", Toast.LENGTH_SHORT).show()
                }
            }
            scoreText.text = getScore(user).toString()
        }


        return view
    }

    private fun addScore(score : Int, user : String) {
        var shared = getScore(user)
        shared += score
        setScore(shared, user)
    }

    private fun getScore(user : String): Int {
        val sharedScore = requireActivity().getSharedPreferences("com.example.myapplication.shared",0)
        return if (sharedScore.contains(user))
            sharedScore.getInt(user, 0)
        else 0
    }

    private fun setScore(score : Int, user : String) {
        val sharedScore = requireActivity().getSharedPreferences("com.example.myapplication.shared", 0)
        val edit = sharedScore.edit()
        edit.putInt(user, score)
        edit.apply()
    }

    private fun getHighScore(db : ScoreDB, user : String): Int {
        return if (db.hasScore(user))
            db.getHighScore(user)
        else 0
    }

    private fun setFinalScore(username : String, highScore : Int) {
        db.addScore(username, highScore)
        Thread {
            run {
                pgdb.putScore(Score(0, username, highScore))
            }
        }.start()
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }


}