package com.example.online_health_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.online_health_app.R
import com.example.online_health_app.data.MessageItem

class MessageAdapter(
    private val messageList: List<MessageItem>
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    // Interface for handling item clicks
    interface OnMessageItemClickListener {
        fun onItemClick(doctorName: String)
    }

    private var itemClickListener: OnMessageItemClickListener? = null

    fun setOnItemClickListener(listener: OnMessageItemClickListener) {
        itemClickListener = listener
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.imageViewProfile)
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val lastMessageTextView: TextView = itemView.findViewById(R.id.textViewMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_card, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.profileImage.setImageResource(message.profileImage)
        holder.nameTextView.text = message.name
        holder.lastMessageTextView.text = message.lastMessage

        // Set click listener on the item view
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(message.name) // Pass the name of the doctor
        }
    }
}
