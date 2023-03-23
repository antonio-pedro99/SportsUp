package com.sigma.sportsup.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sigma.sportsup.R
import com.sigma.sportsup.data.GameModel

class GamesItemAdapter(private val context:Context, private val games: List<GameModel>):RecyclerView.Adapter<GamesItemAdapter.GamesItemAdapterViewHolder>() {

    class GamesItemAdapterViewHolder(view:View):RecyclerView.ViewHolder(view){
        val gameTextView:TextView = view.findViewById(R.id.game_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesItemAdapterViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.game_recyclerview_item, parent, false);
        return  GamesItemAdapterViewHolder(view)
    }

    override fun getItemCount(): Int = games.size

    override fun onBindViewHolder(holder: GamesItemAdapterViewHolder, position: Int) {
        val item = games.get(position)
        holder.gameTextView.text = item.name
    }
}