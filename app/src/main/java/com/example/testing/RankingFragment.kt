package com.example.testing

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.testing.data.ScoreDB

class RankingFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var db : ScoreDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ScoreDB(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()
        recyclerView = requireView().findViewById(R.id.recyclerView)
        recyclerAdapter = RecyclerAdapter(db.getTopScores(), requireContext())

        refreshLayout = requireView().findViewById(R.id.swiperefresh)
        refreshLayout.setOnRefreshListener {
            recyclerAdapter.updateItems(db.getTopScores())
            recyclerAdapter.notifyDataSetChanged()
            Handler().postDelayed(Runnable {
                refreshLayout.isRefreshing = false
            }, 500)
        }

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        parentFragmentManager.setFragmentResultListener("updateRecyclerList", requireActivity()) { requestKey, bundle ->
            recyclerAdapter.updateItems(db.getTopScores())
            recyclerAdapter.notifyDataSetChanged()
        }
    }
}