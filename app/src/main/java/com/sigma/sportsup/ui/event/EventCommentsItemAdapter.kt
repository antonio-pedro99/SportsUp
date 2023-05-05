package com.sigma.sportsup.ui.event

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sigma.sportsup.R
import com.sigma.sportsup.data.Comment
import org.w3c.dom.Text

class EventCommentsItemAdapter(private val context: Context, private val comments: List<Comment>) :
    RecyclerView.Adapter<EventCommentsItemAdapter.EventCommentItemViewHolderAdapter>() {
    class EventCommentItemViewHolderAdapter(view: View) : RecyclerView.ViewHolder(view) {
        val commentUserName: TextView = view.findViewById(R.id.event_comment_item_name)
        val commentDate: TextView = view.findViewById(R.id.event_comment_item_date)
        val comment: TextView = view.findViewById(R.id.event_comment_item_comment)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventCommentItemViewHolderAdapter {
        val view = LayoutInflater.from(context).inflate(R.layout.event_comment_item, parent, false)
        return EventCommentItemViewHolderAdapter(view)
    }

    override fun onBindViewHolder(holder: EventCommentItemViewHolderAdapter, position: Int) {
        val comment = comments[position]

        //Glide.with(context).load(comment.profileImage).into(findViewById(R.id.event_comment_item_profile_image))
        holder.commentUserName.text = comment.name
        holder.commentDate.text = comment.date
        holder.comment.text = comment.comment

    }

    override fun getItemCount(): Int {
        return comments.size
    }
}