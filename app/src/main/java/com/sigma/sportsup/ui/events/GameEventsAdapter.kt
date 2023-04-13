package com.sigma.sportsup.ui.events

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.sigma.sportsup.EventDetailsActivity
import com.sigma.sportsup.R
import com.sigma.sportsup.data.GameEvent
import org.w3c.dom.Text

class GameEventItemAdapter(private val context: Context, private val gamesEvents: List<GameEvent>) : RecyclerView.Adapter<GameEventItemAdapter.GameEventItemAdapterViewHolder>() {

    class GameEventItemAdapterViewHolder(view: View): RecyclerView.ViewHolder(view){
        val nameTextView: TextView = view.findViewById<TextView>(R.id.event_game)
        val timeTextView: TextView = view.findViewById<TextView>(R.id.event_time)
        val dateTextView: TextView = view.findViewById<TextView>(R.id.event_date)
        val buddiesTextView: TextView = view.findViewById<TextView>(R.id.event_number_participants)
        val venueTextView: TextView = view.findViewById<TextView>(R.id.event_venue)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameEventItemAdapterViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.event_recyclerview_item, parent, false)
        return  GameEventItemAdapterViewHolder(root)
    }

    override fun getItemCount(): Int = gamesEvents.size

    override fun onBindViewHolder(holder: GameEventItemAdapterViewHolder, position: Int) {
        val item = gamesEvents[position]

        val numberOfBuddiesString = context.resources.getString(R.string.event_players, item.current_players ?: "1",item.number_of_players)
        holder.buddiesTextView.text = numberOfBuddiesString
        holder.dateTextView.text = item.date
        holder.timeTextView.text = item.time
        holder.venueTextView.text = item.venue
        holder.nameTextView.text = item.name

        holder.itemView.setOnClickListener {
            val intent = Intent(context, EventDetailsActivity::class.java)
            intent.putExtra("eventId", item.id!!)
            intent.putExtra("eventName", item.name)
            context.startActivity(intent)

           // it.findNavController().navigate(R.id.action_nav_events_to_navigation_event_activity)
        }
    }
}