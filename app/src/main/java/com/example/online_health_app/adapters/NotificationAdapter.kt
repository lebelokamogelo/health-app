package com.example.online_health_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.online_health_app.R
import com.example.online_health_app.data.Notification

class NotificationAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.imageViewIcon)
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val messageTextView: TextView = itemView.findViewById(R.id.textViewMessage)
        val timeTextView: TextView = itemView.findViewById(R.id.textViewTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_card, parent, false)
        return NotificationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        //holder.iconImageView.setImageResource(notification.icon)
        holder.nameTextView.text = notification.name
        holder.messageTextView.text = notification.message
        holder.timeTextView.text = notification.time
    }
}
