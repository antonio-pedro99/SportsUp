package com.sigma.sportsup.ui.event

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sigma.sportsup.R
import com.sigma.sportsup.data.UserModel

class EventPlayersItemAdapter(private val context: Context, private  val players:List<UserModel>):RecyclerView.Adapter<EventPlayersItemAdapter.EventPlayersItemAdapterViewHolder>() {

    class EventPlayersItemAdapterViewHolder(view: View):RecyclerView.ViewHolder(view){
        val playerNameTxt :TextView = view.findViewById(R.id.txt_player_name)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventPlayersItemAdapterViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.players_recycler_view_item, parent, false)
        return  EventPlayersItemAdapterViewHolder(root)
    }

    override fun getItemCount(): Int = players.size

    override fun onBindViewHolder(holder: EventPlayersItemAdapterViewHolder, position: Int) {
        val item = players[position]
        holder.playerNameTxt.text = item.name
    }
}