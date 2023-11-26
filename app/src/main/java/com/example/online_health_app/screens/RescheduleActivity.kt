package com.example.online_health_app.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.online_health_app.MainActivity
import com.example.online_health_app.R
import com.example.online_health_app.databinding.ActivityRescheduleBinding
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RescheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRescheduleBinding
    private var selectedCalendarDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRescheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uuid = intent.getStringExtra("uuid")
        val id = intent.getStringExtra("id")

        val calendarView: CalendarView = findViewById(R.id.calendarView)

        val minDate = Calendar.getInstance()
        minDate.add(Calendar.DAY_OF_MONTH, 3)
        calendarView.minDate = minDate.timeInMillis

        selectedCalendarDate =
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(minDate.time)

        binding.update.setOnClickListener {

            binding.update.visibility = View.GONE
            binding.loading.visibility = View.VISIBLE

            FirebaseFirestore.getInstance()
                .collection("appointments")
                .document(uuid.toString())
                .collection("appointment")
                .document(id.toString())
                .update(
                    "date",
                    selectedCalendarDate
                )
                .addOnSuccessListener {

                    binding.update.visibility = View.VISIBLE
                    binding.loading.visibility = View.GONE

                    Toasty.success(this, "Successfully updated", Toast.LENGTH_SHORT, true).show()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {

                    binding.update.visibility = View.VISIBLE
                    binding.loading.visibility = View.GONE

                    Toasty.warning(
                        this,
                        "Something went wrong. Try again",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
        }

        // Set up the calendar view
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val formattedDate =
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selectedDate.time)

            selectedCalendarDate = formattedDate

        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}