package com.example.testing

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.testing.data.PostgresHandler
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainScreenActivity : AppCompatActivity() {
    private lateinit var viewPager : ViewPager2
    private lateinit var viewPagerAdapter : ViewPagerStateAdapter
    private lateinit var tabLayout : TabLayout
    private lateinit var user : String
    private lateinit var builder : AlertDialog.Builder
    private var db = PostgresHandler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        user = if (intent.hasExtra("user"))
            intent.getStringExtra("user").toString()
            else "HOW_DID_YOU_GET_HERE"
        viewPager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tabLayout)
        setTitle("Guess the Number | $user")

        builder = AlertDialog.Builder(this)
        builder.setTitle("Guess the Number")
        builder.setNegativeButton("NO") {_, _ ->}
        builder.setPositiveButton("YES") {
                _, _ ->
            val shared = getSharedPreferences("com.example.testing.shared", 0)
            shared.edit()
                .remove("username")
                .remove("password")
                .apply()
            finish()
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        viewPagerAdapter = ViewPagerStateAdapter(this)
        createViewPager()
    }

    private fun createViewPager() {
        viewPagerAdapter.addFragment(GameFragment(user),"Game")
        viewPagerAdapter.addFragment(RankingFragment(), "Local Ranking")
        viewPagerAdapter.addFragment(GlobalRankingFragment(), "Global Ranking")

        viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) {
                tab, pos ->
            tab.text = viewPagerAdapter.getTitle(pos)
        }.attach()
    }

    fun getPostgresDatabase() : PostgresHandler {
        return db
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.log_out -> {
                val shared = getSharedPreferences("com.example.testing.shared", 0)
                shared.edit().remove("username").remove("password").apply()
                finish()
            }
            R.id.twitter_share -> {
                val score = db.getHighScore(user)
                val msg = "I got score of $score in Guess the Number [Project for Politechnika Poznańska]"
                shareOnTwitter(msg)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareOnTwitter(message : String) {
        val tweetUrl = ("https://twitter.com/intent/tweet?text=$message")
        val uri = Uri.parse(tweetUrl)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}