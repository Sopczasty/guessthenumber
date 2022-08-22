package com.example.testing

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testing.data.model.Score

class RecyclerAdapter(private var itemList: List<Score>?, private val context: Context)
    : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var mx = 0
    private var curPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.score_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position == 0) {
            mx = itemList!![position].score
            curPos = 1
        }
        var score = itemList!![position]
        if(score.score == mx) score.pos = curPos
        else {
            score.pos = ++curPos
            mx = score.score
        }
        if(score.score > 0)
        when (score.pos) {
            1 -> holder.pos.setBackgroundColor(context.getColor(R.color.gold))
            2 -> holder.pos.setBackgroundColor(context.getColor(R.color.silver))
            3 -> holder.pos.setBackgroundColor(context.getColor(R.color.bronze))
            else -> holder.pos.setBackgroundColor(context.getColor(R.color.white))
        }
        else holder.pos.setBackgroundColor(context.getColor(R.color.white))
        holder.pos.text = score.pos.toString()
        holder.name.text = score.name
        holder.score.text = score.score.toString()
        //holder.scoreImage.setImageDrawable(context.getDrawable(context.resources.getIdentifier(route.image, "drawable", context.packageName)))
    }

    override fun getItemCount(): Int {
        return if (itemList != null) itemList!!.size else 0
    }

    fun updateItems(itemList: List<Score>?) {
        this.itemList = itemList
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val pos: TextView = itemView.findViewById(R.id.pos)
        val name: TextView = itemView.findViewById(R.id.name)
        val score: TextView = itemView.findViewById(R.id.score)
        //val routeImage: ImageView = itemView.findViewById(R.id.routeImage)
    }
}