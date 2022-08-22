package com.example.testing

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ObjectAnimator.ofFloat
import android.animation.ObjectAnimator.ofInt
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.doOnEnd
import com.example.testing.ui.login.LoginActivity
import java.sql.Time
import java.sql.Timestamp
import java.util.*
import kotlin.random.Random
import kotlin.collections.ArrayList

class LogoScreen : AppCompatActivity() {
    private lateinit var numberViews: ArrayList<TextView>
    private lateinit var deltaTimes: ArrayList<Long>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo_screen)

        deltaTimes = ArrayList()
        deltaTimes.add(400)
        deltaTimes.add(500)
        deltaTimes.add(600)
        deltaTimes.add(700)
        deltaTimes.add(800)
        deltaTimes.add(900)
        deltaTimes.add(1000)

        numberViews = ArrayList(20)
        numberViews.add(findViewById(R.id.a1))
        numberViews.add(findViewById(R.id.a2))
        numberViews.add(findViewById(R.id.a3))
        numberViews.add(findViewById(R.id.a4))
        numberViews.add(findViewById(R.id.a5))
        numberViews.add(findViewById(R.id.a6))
        numberViews.add(findViewById(R.id.a7))
        numberViews.add(findViewById(R.id.a8))
        numberViews.add(findViewById(R.id.a9))
        numberViews.add(findViewById(R.id.a10))
        numberViews.add(findViewById(R.id.a11))
        numberViews.add(findViewById(R.id.a12))
        numberViews.add(findViewById(R.id.a13))
        numberViews.add(findViewById(R.id.a14))
        numberViews.add(findViewById(R.id.a15))
        numberViews.add(findViewById(R.id.a16))
        numberViews.add(findViewById(R.id.a17))
        numberViews.add(findViewById(R.id.a18))
        numberViews.add(findViewById(R.id.a19))
        numberViews.add(findViewById(R.id.a20))

        for (t in numberViews) t.alpha = 0f

        val anim = ofFloat(findViewById<ImageView>(R.id.imageView), "translationY", -1500f, 0f)
            .apply {
                duration = 2500
                interpolator = BounceInterpolator()
            }
        val anis = AnimatorSet()
        val fullAnimator = animateRandomNumbers()
        anis.playTogether(anim, fullAnimator)
        anis.doOnEnd {
            Thread {
                run{
                    finish()
                }
                runOnUiThread {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)

                }
            }.start()

        }
        anis.start()

        }

    private fun animateRandomNumbers(): AnimatorSet {
        val seed = Random
        numberViews.shuffle(seed)
        val animSet = AnimatorSet()
        animSet.playTogether(
            singleAnimation(numberViews[0], seed),
            singleAnimation(numberViews[1], seed),
            singleAnimation(numberViews[2], seed),
            singleAnimation(numberViews[3], seed),
            singleAnimation(numberViews[4], seed),
            singleAnimation(numberViews[5], seed),
            singleAnimation(numberViews[6], seed),
            singleAnimation(numberViews[7], seed),
            singleAnimation(numberViews[8], seed),
            singleAnimation(numberViews[9], seed),
            singleAnimation(numberViews[10], seed)
            )
        return animSet
    }

    private fun singleAnimation(view: TextView, r : Random): AnimatorSet {
        val start = r.nextLong(1000)
        val dt = deltaTimes.random(r) + 1000
        val alphaAnimator = ofFloat(view, "alpha", 1f).apply {
            interpolator = BounceInterpolator()
            duration = dt
            startDelay = start
        }
        val scaleAnimatorX = ofFloat(view, "scaleX", 0f, 1f).apply {
            interpolator = BounceInterpolator()
            duration = dt
            startDelay = start
        }

        val scaleAnimatorY = ofFloat(view, "scaleY", 0f, 1f).apply {
            interpolator = BounceInterpolator()
            duration = dt
            startDelay = start
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimator, scaleAnimatorX, scaleAnimatorY)
        return animatorSet
    }
}