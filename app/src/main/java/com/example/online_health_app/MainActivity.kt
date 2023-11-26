package com.example.online_health_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.online_health_app.data.AvailableDoctor
import com.example.online_health_app.databinding.ActivityMainBinding
import com.example.online_health_app.fragments.AppointmentFragment
import com.example.online_health_app.fragments.HomeFragment
import com.example.online_health_app.fragments.MessagesFragment
import com.example.online_health_app.fragments.NotificationFragment
import com.example.online_health_app.fragments.SettingFragment
import com.example.online_health_app.model.AppDatabase
import com.example.online_health_app.model.AvailableDoctorTable
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    // Create a Job reference to keep track of the coroutine
    private var dataLoadingJob: Job? = null

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        // Start the coroutine and keep a reference to the Job
        dataLoadingJob = initDataLoading()

        // Set the default selected item to Home
        binding.menu.setItemSelected(R.id.home)

        binding.menu.setOnItemSelectedListener {

            when (it) {

                R.id.home -> replaceFragment(HomeFragment())
                R.id.appointment -> replaceFragment(AppointmentFragment())
                R.id.notification -> replaceFragment(NotificationFragment())
                R.id.settings -> replaceFragment(SettingFragment())
                R.id.messages -> replaceFragment(MessagesFragment())

                else -> {
                    replaceFragment(HomeFragment())
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_layout, fragment)

        val delayMillis = 175L
        GlobalScope.launch(Dispatchers.Main) {
            delay(delayMillis)
            fragmentTransaction.commit()
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initDataLoading(): Job {
        // Start the coroutine and return the Job
        return GlobalScope.launch(Dispatchers.Main) {
            // 175 millisecond delay
            delay(175)

            // Check if the coroutine is still active before proceeding
            if (isActive) {
                loadAvailableDoctors()
            }
        }
    }


    private suspend fun loadAvailableDoctors() {
        // Create a Fire store instance.
        val store = FirebaseFirestore.getInstance()

        // Create a Query object that get all the doctors
        val query = store.collection("doctors")

        // Execute the query and get the results.
        val results = query.get().await()


        // Get the database instance
        val database = AppDatabase.getInstance(this@MainActivity)

        // Store the available doctors in Room
        val scope = CoroutineScope(Dispatchers.IO)


        for (data in results) {
            val doctor = data.toObject(AvailableDoctor::class.java)

            val availableDoctorTable = AvailableDoctorTable(
                uuid = doctor.uuid,
                name = doctor.name,
                specializing = doctor.specializing,
                rating = doctor.rating,
                experience = doctor.experience,
                image = doctor.image
            )


            scope.launch {
                database.availableDoctorDao.insertAvailableDoctor(availableDoctorTable)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the coroutine when the view is destroyed
        dataLoadingJob?.cancel()
    }
}