package com.example.online_health_app.screens

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.online_health_app.adapters.ChatAdapter
import com.example.online_health_app.data.Message
import com.example.online_health_app.databinding.ActivityChatBinding
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChatActivity : AppCompatActivity(), ChatAdapter.MessageLoadListener {

    private lateinit var binding: ActivityChatBinding
    private val client = OkHttpClient()

    private val messageList: ArrayList<Message> = ArrayList()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()
        val messagesRef = db.collection("messages")

        val sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        val uuid = sharedPreferences.getString("uuid", "")

        // Get the data from the Intent extras
        val name = intent.getStringExtra("name")
        binding.doctorName.text = name

        binding.back.setOnClickListener {
            finish()
        }

        binding.editTextMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Enable or disable the button based on the text in the EditText
                binding.buttonSend.visibility =
                    if (s?.trim().isNullOrEmpty()) View.INVISIBLE else View.VISIBLE

            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed
            }
        })

        binding.buttonSend.setOnClickListener {

            // Handle the button click event here
            val messageText = binding.editTextMessage.text.toString()

            val message = Message("openai", uuid.toString(), messageText, getCurrentTimeAndDate())

            messagesRef.document(uuid.toString()).collection("chats").add(message)
                .addOnSuccessListener {
                    // Message added successfully
                    binding.editTextMessage.text.clear() // Clear the EditText

                    getResponse(messageText) { response ->
                        runOnUiThread {

                            val messageBot =
                                Message(
                                    uuid.toString(),
                                    "openai",
                                    response.trim(),
                                    getCurrentTimeAndDate()
                                )
                            //binding.textResponse.text = response
                            messagesRef.document(uuid.toString()).collection("chats")
                                .add(messageBot)

                            val itemCount = chatAdapter.itemCount
                            binding.recyclerViewMessages.smoothScrollToPosition(itemCount - 1)
                        }
                    }
                }
                .addOnFailureListener { _ ->
                    // Error adding message
                    //Log.e(TAG, "Error adding message", e)

                    Toasty.info(this, "Error adding message", Toast.LENGTH_SHORT).show()
                }
        }

        chatAdapter = ChatAdapter(this)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.layoutManager = layoutManager
        binding.recyclerViewMessages.adapter = chatAdapter

        messagesRef.document(uuid.toString()).collection("chats").orderBy("time")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                messageList.clear()

                for (document in snapshot!!) {
                    if (document.exists()) {

                        val messageData = document.toObject(Message::class.java)
                        messageList.add(messageData)
                    }
                }

                // Set messages in the adapter once they are fully loaded
                chatAdapter.setMessages(messageList)
            }
    }

    override fun messageLoadComplete() {
        // Callback triggered when all messages are loaded
        // You can perform any necessary UI updates here
    }

    private fun getResponse(question: String, callback: (String) -> Unit) {

        val apiKey = YOUR_OPENAI_KEYS
        val url = "https://api.openai.com/v1/engines/text-davinci-003/completions"

        val prompt = """
        Hello! I'm your personal assistant, ready to discuss any health-related questions. If you want to start a new conversation or have a different topic, simply greet me, and we'll begin a fresh interaction. Ask anything you'd typically ask a doctor, and I'll provide the information you need. Thanks
    """.trimIndent()

        val requestBody = """
        {
        "prompt": "$prompt\n$question",
        "max_tokens": 500,
        "temperature": 0,
        "top_p": 0
        }
    """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    Log.v("data", body)
                } else {
                    Log.v("data", "empty")
                }
                val jsonObject = body?.let { JSONObject(it) }
                val jsonArray: JSONArray = jsonObject!!.getJSONArray("choices")
                val textResult = jsonArray.getJSONObject(0).getString("text")
                callback(textResult)
            }
        })
    }

    private fun getCurrentTimeAndDate(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.getDefault())
        return dateFormat.format(currentTime)
    }

}
