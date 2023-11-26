package com.example.online_health_app.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.online_health_app.R
import com.example.online_health_app.adapters.AvailableDoctorAdapter
import com.example.online_health_app.adapters.CategoryAdapter
import com.example.online_health_app.data.AvailableDoctor
import com.example.online_health_app.data.Category
import com.example.online_health_app.databinding.FragmentHomeBinding
import com.example.online_health_app.model.AppDatabase
import com.example.online_health_app.screens.BodyMassActivity
import com.example.online_health_app.screens.BreatheActivity
import com.example.online_health_app.screens.DoctorDetailsActivity
import com.example.online_health_app.screens.HeartRateActivity
import com.example.online_health_app.screens.MenstrualCycleActivity
import com.example.online_health_app.screens.ProfileActivity
import com.example.online_health_app.screens.SearchActivity
import com.example.online_health_app.screens.SymptomsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var availableDoctorAdapter: AvailableDoctorAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    // Create a Job reference to keep track of the coroutine
    private var dataLoadingJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        val sharedPreferences =
            requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)

        binding.AvatarImageView.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        binding.username.text = sharedPreferences.getString("name", "")
        initCategories()

        recyclerView = binding.availableDoctor
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        // Load the data from Firebase.
        val availableDoctorList = mutableListOf<AvailableDoctor>()
        //availableDoctorList.addAll(loadAvailableDoctors())

        // Get the database instance
        val database = AppDatabase.getInstance(requireContext())

        // Store the available doctors in Room
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {

            val data = database.availableDoctorDao.getAllAvailableDoctors()

            for (it in data) {

                val availableDoctorTable = AvailableDoctor(
                    uuid = it.uuid,
                    name = it.name,
                    specializing = it.specializing,
                    rating = it.rating,
                    experience = it.experience,
                    image = it.image
                )

                availableDoctorList.add(availableDoctorTable)
            }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            // Set the adapter for the RecyclerView.
            availableDoctorAdapter = AvailableDoctorAdapter(availableDoctorList)
            recyclerView.adapter = availableDoctorAdapter

            // Set an OnItemClickListener for the RecyclerView.
            availableDoctorAdapter.setOnItemClickListener(object :
                AvailableDoctorAdapter.OnItemClickListener {
                override fun onItemClick(doctor: AvailableDoctor) {
                    val intent = Intent(context, DoctorDetailsActivity::class.java)
                    intent.putExtra("doctorName", doctor.name)
                    intent.putExtra("doctorSpecializing", doctor.specializing)
                    intent.putExtra("doctorRating", doctor.rating)
                    intent.putExtra("doctorExperience", doctor.experience)
                    intent.putExtra("doctorImage", doctor.image)
                    intent.putExtra("uuid", doctor.uuid)
                    startActivity(intent)
                }
            })
        }, 100)

        binding.viewAll.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initCategories() {
        recyclerView = binding.categories
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        val categoryList = mutableListOf(
            Category(R.drawable.virus, "Symptoms"),
            Category(R.drawable.bmi, "BMI"),
            Category(R.drawable.menstrual_cycle, "Cycle"),
            Category(R.drawable.heart, "Heart Rate"),
            Category(R.drawable.lungs, "Breathing")
        )

        categoryAdapter = CategoryAdapter(categoryList)
        recyclerView.adapter = categoryAdapter

        categoryAdapter.setOnCategoryClickListener(object :
            CategoryAdapter.OnCategoryClickListener {
            override fun onCategoryClick(category: Category) {
                when (category.title) {
                    "BMI" -> startActivity(Intent(context, BodyMassActivity::class.java))
                    "Heart Rate" -> startActivity(Intent(context, HeartRateActivity::class.java))
                    "Symptoms" -> startActivity(Intent(context, SymptomsActivity::class.java))
                    "Breathing" -> startActivity(Intent(context, BreatheActivity::class.java))
                    "Cycle" -> startActivity(Intent(context, MenstrualCycleActivity::class.java))
                    else -> Toast.makeText(context, category.title, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel the coroutine when the view is destroyed
        dataLoadingJob?.cancel()
        _binding = null
    }
}


