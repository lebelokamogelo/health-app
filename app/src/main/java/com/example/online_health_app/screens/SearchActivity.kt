package com.example.online_health_app.screens

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.online_health_app.adapters.ListAdapter
import com.example.online_health_app.data.AvailableDoctor
import com.example.online_health_app.databinding.ActivitySearchBinding
import com.example.online_health_app.model.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.ListRecycleView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Load the data from Firebase.
        val availableDoctorList = mutableListOf<AvailableDoctor>()
        //availableDoctorList.addAll(loadAvailableDoctors())

        // Get the database instance
        val database = AppDatabase.getInstance(this)

        binding.back.setOnClickListener {
            finish()
        }

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

            listAdapter = ListAdapter(availableDoctorList)
            recyclerView.adapter = listAdapter

        }

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    // If the search query is empty, show all items in the list
                    listAdapter.filterList(availableDoctorList)
                    binding.ListRecycleView.visibility = View.VISIBLE
                    binding.notFound.visibility = View.GONE
                } else {
                    // Filter based on the search query
                    val filteredList = availableDoctorList.filter { doctor ->
                        doctor.name.contains(
                            newText,
                            ignoreCase = true
                        ) || doctor.specializing.contains(newText, ignoreCase = true)
                    }

                    if (filteredList.isEmpty()) {
                        binding.notFound.visibility = View.VISIBLE
                        binding.ListRecycleView.visibility = View.GONE
                    } else {
                        listAdapter.filterList(filteredList as MutableList<AvailableDoctor>)

                        binding.notFound.visibility = View.GONE
                        binding.ListRecycleView.visibility = View.VISIBLE
                    }
                }
                return true
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }
}