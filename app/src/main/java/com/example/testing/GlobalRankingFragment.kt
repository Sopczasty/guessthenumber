package com.example.testing

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.testing.data.PostgresHandler
import com.example.testing.data.ScoreDB
import com.example.testing.data.model.Score

class GlobalRankingFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var loadingData : ProgressBar
    private lateinit var builder : AlertDialog.Builder
    private lateinit var db : PostgresHandler
    private var scores: ArrayList<Score>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = (context as MainScreenActivity).getPostgresDatabase()
        builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Failed to load data")
        builder.setMessage("Try reloading later or check your internet connection.")
        builder.setPositiveButton("OK") {_,_->}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_global_ranking, container, false)
        loadingData = root.findViewById(R.id.loadingDataBar)
        recyclerView = root.findViewById(R.id.recyclerView)
        refreshLayout = root.findViewById(R.id.swiperefresh)
        return root
    }

    private fun cleanScores(scores: ArrayList<Score>): ArrayList<Score> {
        for (i in 0..scores.size) {
            for(j in i+1..scores.size) {
                if(i < scores.size && j < scores.size)
                    if (scores[i].name == scores[j].name) scores.removeAt(j)
            }
        }
        return scores
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getGlobalScores() {
        Thread {
            run {
                scores = db.getScores()
            }
            requireActivity().runOnUiThread {
                if (scores == null) builder.show()
                else {
                    recyclerAdapter.updateItems(cleanScores(scores!!))
                    recyclerAdapter.notifyDataSetChanged()
                }
                refreshLayout.isRefreshing = false
                loadingData.visibility = View.GONE
            }
        }.start()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()
        getGlobalScores()
        recyclerAdapter = RecyclerAdapter(scores, requireContext())

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        refreshLayout.setOnRefreshListener {
            getGlobalScores()
        }

        parentFragmentManager.setFragmentResultListener("updateRecyclerList", requireActivity()) { requestKey, bundle ->
            getGlobalScores()
        }
    }
}