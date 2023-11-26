package com.example.online_health_app.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.online_health_app.adapters.AppointmentAdapter
import com.example.online_health_app.data.Appointment
import com.example.online_health_app.databinding.FragmentAppointmentBinding
import com.example.online_health_app.screens.RescheduleActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!

    // Create a Job reference to keep track of the coroutine
    private var dataLoadingJob: Job? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var appointmentAdapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)

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
                initAppointments()
                // Hide loading indicator after data is loaded
                binding.loading.visibility = View.GONE
            }
        }
    }

    private suspend fun loadAppointments(): Collection<Appointment> {

        val sharedPreferences =
            requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
        val uuid = sharedPreferences.getString(
            "uuid",
            ""
        )

        // Create a store instance.
        val store = FirebaseFirestore.getInstance()

        // Create a Query object
        val query = store.collection("appointments").document(uuid.toString()).collection("appointment")

        // Execute the query and get the results.
        val results = query.get().await()

        // Add the results to the appointment variable.
        val appointmentList: MutableList<Appointment> = mutableListOf()

        for (document in results) {
            // Get the document ID
            val appointmentId = document.id

            // Get the appointment
            val appointment = document.toObject(Appointment::class.java)


            // Add the appointment to the appointmentList if it is not null.
            if (uuid == appointment.uuid
                    ) {
                appointmentList.add(appointment)
            }
        }
        Log.d("kamogelo", appointmentList.toString())
        return appointmentList
    }

    private suspend fun initAppointments() {

        // Load the data from Firebase.
        val appointments = loadAppointments()


        if (appointments.isEmpty()) {
            binding.notFound.visibility = View.VISIBLE
        } else {
            binding.notFound.visibility = View.GONE

            recyclerView = binding.appointmentRecycleView
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)

            appointmentAdapter = AppointmentAdapter(appointments.toList())
            recyclerView.adapter = appointmentAdapter

            appointmentAdapter.setOnItemClickListener(
                object : AppointmentAdapter.OnItemClickListener {
                    override fun onItemClick(appointment: Appointment) {

                        val intent = Intent(context, RescheduleActivity::class.java)
                        intent.putExtra("uuid", appointment.uuid)
                        intent.putExtra("id", appointment.id)
                        startActivity(intent)
                    }

                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel the coroutine when the view is destroyed
        dataLoadingJob?.cancel()
        _binding = null
    }
}