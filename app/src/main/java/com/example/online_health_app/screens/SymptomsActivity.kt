package com.example.online_health_app.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.online_health_app.MainActivity
import com.example.online_health_app.databinding.ActivitySymptomsBinding
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

class SymptomsActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var binding: ActivitySymptomsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySymptomsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Do something when the user submits the query

                binding.loading.visibility = View.VISIBLE

                getResponse(query.toString()) { response ->
                    runOnUiThread {

                        binding.loading.visibility = View.GONE
                        binding.textResponse.text = response
                    }
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return true
            }
        })

    }

    fun getResponse(question: String, callback: (String) -> Unit) {

        binding.search.clearFocus()

        val apiKey = YOUR_OPENAI_KEYS
        val url = "https://api.openai.com/v1/engines/text-davinci-003/completions"

        val prompt = """
        Hello! I'm here to assist you with questions related to various health symptoms. Feel free to ask about symptoms, their causes, treatment options, recommended actions, or when it's advisable to seek medical help. If your question is not related to health symptoms, I apologize, but I'm specifically designed to provide information in this domain. Please ask a health-related question for the most accurate and helpful response. Thank you!
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
}