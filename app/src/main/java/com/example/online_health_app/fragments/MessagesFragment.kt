package com.example.online_health_app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.online_health_app.R
import com.example.online_health_app.adapters.MessageAdapter
import com.example.online_health_app.data.MessageItem
import com.example.online_health_app.databinding.FragmentMessagesBinding
import com.example.online_health_app.screens.ChatActivity

class MessagesFragment : Fragment() {
    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        val messageList = createDummyMessageList()
        val messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter

        messageAdapter.setOnItemClickListener(object : MessageAdapter.OnMessageItemClickListener {
            override fun onItemClick(doctorName: String) {

                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("name", "Personal Assistant")

                startActivity(intent)

            }
        })
        // Inflate the layout for this fragment
        return binding.root
    }

    // Data source for the messages
    private fun createDummyMessageList(): List<MessageItem> {

        return listOf(
            MessageItem("Personal Assistant", R.drawable.robot, "Hello! How are you?"),
            // Add more message items as needed
        )
    }
}