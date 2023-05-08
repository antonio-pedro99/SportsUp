package com.sigma.sportsup.ui.home

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sigma.sportsup.EventDetailsActivity
import com.sigma.sportsup.R
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.SessionEvent
import com.sigma.sportsup.data.UserModel
import org.w3c.dom.Text

class SessionsItemAdapter(private val context: Context, private val currentUser:UserModel, private val sessions: List<GameEvent>, private val onJoinClicked:(GameEvent)->Any, private val onSaveClicked:(Int)->Any) : RecyclerView.Adapter<SessionsItemAdapter.SessionsItemAdapterViewHolder>() {
    class SessionsItemAdapterViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtTitle: TextView = view.findViewById(R.id.session_name)
        val txtVenue: TextView = view.findViewById(R.id.session_venue)
        val txtDate: TextView = view.findViewById(R.id.session_date)
        val txtTime: TextView = view.findViewById(R.id.session_time)
        val btnJoinGame:Button = view.findViewById(R.id.btn_joing_session)
        val btnSaveSession:Button = view.findViewById(R.id.btn_save_session)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SessionsItemAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.session_recyclerview_item, parent, false)
        return  SessionsItemAdapterViewHolder(view)
    }

    override fun getItemCount(): Int = sessions.size

    override fun onBindViewHolder(holder: SessionsItemAdapterViewHolder, position: Int) {
        val item = sessions.get(position)
        holder.txtDate.text = item.date
        holder.txtTime.text = item.start_time
        holder.txtTitle.text =  item.game_event_name?: item.name
        holder.txtVenue.text = item.venue

        holder.itemView.setOnClickListener { val intent = Intent(context, EventDetailsActivity::class.java)
            intent.putExtra("eventId", item.id!!)
            intent.putExtra("eventName", item.name)
            context.startActivity(intent) }

       if (currentUser.id == item.host_ref) {
           holder.btnJoinGame.visibility = View.INVISIBLE
           holder.btnSaveSession.visibility = View.INVISIBLE
       } else   {
           holder.btnJoinGame.setOnClickListener {
               onJoinClicked(item) }
           holder.btnSaveSession.setOnClickListener { onSaveClicked(position) }
       }
    }


}