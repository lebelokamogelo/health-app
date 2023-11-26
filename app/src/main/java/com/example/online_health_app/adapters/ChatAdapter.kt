package com.example.online_health_app.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.online_health_app.R
import com.example.online_health_app.data.Message
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter(private val messageLoadListener: MessageLoadListener) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private val messages: MutableList<Message> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
        messageLoadListener.messageLoadComplete()
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.textViewChatMessage)
        val timeTextView: TextView = itemView.findViewById(R.id.textViewChatTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutResId = when (viewType) {
            RECEIVED_MESSAGE_VIEW_TYPE -> R.layout.item_message_received
            else -> R.layout.item_message_sent
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.messageTextView.text = message.text
        holder.timeTextView.text = extractHHMM(message.time)

    }

    private fun extractHHMM(input: String): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.getDefault())
        val parsedDate = dateFormat.parse(input)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(parsedDate!!)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]

        return if (message.sender == "openai") {
            RECEIVED_MESSAGE_VIEW_TYPE
        } else {
            SENT_MESSAGE_VIEW_TYPE
        }
    }

    interface MessageLoadListener {
        fun messageLoadComplete()
    }

    companion object {
        private const val RECEIVED_MESSAGE_VIEW_TYPE = 0
        private const val SENT_MESSAGE_VIEW_TYPE = 1
    }
}
