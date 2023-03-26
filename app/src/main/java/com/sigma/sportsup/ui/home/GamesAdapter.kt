package com.sigma.sportsup.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sigma.sportsup.R
import com.sigma.sportsup.data.GameModel

class GamesItemAdapter(private val context:Context, private val games: List<GameModel>):RecyclerView.Adapter<GamesItemAdapter.GamesItemAdapterViewHolder>() {

    class GamesItemAdapterViewHolder(view:View):RecyclerView.ViewHolder(view){
        val gameTextView:TextView = view.findViewById(R.id.game_textview)
        val gameImageView:ImageView = view.findViewById(R.id.game_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesItemAdapterViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.game_recyclerview_item, parent, false);
        return  GamesItemAdapterViewHolder(view)
    }

    override fun getItemCount(): Int = games.size

    override fun onBindViewHolder(holder: GamesItemAdapterViewHolder, position: Int) {
        val item = games.get(position)
        if (item.name != "all") {
            holder.gameTextView.text = item.name
            if (item.image.isNullOrEmpty()) {
                holder.gameImageView.setImageResource(R.drawable.icons8_basketball)
            } else {
                Glide.with(context).load(item.image).into(holder.gameImageView)
            }
        }
    }
}