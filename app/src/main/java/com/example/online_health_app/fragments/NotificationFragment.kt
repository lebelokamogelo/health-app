package com.example.online_health_app.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.online_health_app.adapters.NotificationAdapter
import com.example.online_health_app.data.Notification
import com.example.online_health_app.databinding.FragmentNotificationBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    // Create a Job reference to keep track of the coroutine
    private var dataLoadingJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        // Start the coroutine and keep a reference to the Job
        dataLoadingJob = initDataLoading()

        // Show a loading indicator
        binding.loading.visibility = View.VISIBLE

        // Inflate the layout for this fragment
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initDataLoading(): Job {
        // Start the coroutine and return the Job
        return GlobalScope.launch(Dispatchers.Main) {
            // 175 millisecond delay
            delay(175)

            // Check if the coroutine is still active before proceeding
            if (isActive) {
                initNotifications()
                // Hide loading indicator after data is loaded
                binding.loading.visibility = View.GONE
            }
        }
    }

    private suspend fun loadNotification(): Collection<Notification> {
        // Create a store instance.
        val store = FirebaseFirestore.getInstance()

        // Create a Query object
        val query = store.collection("notifications")

        // Execute the query and get the results.
        val results = query.get().await()

        // Add the results to the appointment variable.
        val notificationList: MutableList<Notification> = mutableListOf()

        for (document in results) {
            // Get the document ID
            val notificationId = document.id

            // Get the appointment
            val notification = document.toObject(Notification::class.java)

            val sharedPreferences =
                requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
            Log.d("kamogelo", notificationList.toString())
            // Add the appointment to the appointmentList if it is not null.
            if (notificationId.trim() == sharedPreferences.getString(
                    "uuid",
                    ""
                )
            ) {
                notificationList.add(notification)
            }
        }


        return notificationList
    }

    private suspend fun initNotifications() {

        // Load the data from Firebase.
        val notifications = loadNotification()

        if (notifications.isEmpty()) {
            binding.notFound.visibility = View.VISIBLE
        } else {
            binding.notFound.visibility = View.GONE

            val notificationAdapter = NotificationAdapter(notifications.toList())

            // Set the adapter to the RecyclerView
            binding.notificationRecycleView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = notificationAdapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel the coroutine when the view is destroyed
        dataLoadingJob?.cancel()
        _binding = null
    }
}