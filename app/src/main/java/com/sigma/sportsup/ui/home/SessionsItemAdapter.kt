package com.sigma.sportsup.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sigma.sportsup.data.SessionEvent

class SessionsItemAdapter(private val context: Context, private val sessions: List<SessionEvent>) : RecyclerView.Adapter<SessionsItemAdapter.SessionsItemAdapterViewHolder>() {
    class SessionsItemAdapterViewHolder(view: View):RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SessionsItemAdapterViewHolder {
      return  SessionsItemAdapterViewHolder(parent)
    }

    override fun getItemCount(): Int = sessions.size

    override fun onBindViewHolder(holder: SessionsItemAdapterViewHolder, position: Int) {
        TODO("Not yet implemented")
    }


}