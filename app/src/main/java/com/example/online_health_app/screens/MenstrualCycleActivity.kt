package com.example.online_health_app.screens

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.online_health_app.MainActivity
import com.example.online_health_app.databinding.ActivityMenstrualCycleBinding

import java.text.SimpleDateFormat
import java.util.Locale

class MenstrualCycleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenstrualCycleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenstrualCycleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MONTH, -1)
        val minDate = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val maxDate = calendar.timeInMillis

        binding.calendar.minDate = minDate
        binding.calendar.maxDate = maxDate

        binding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->

            // Ovulation date
            val ovulationDate = Calendar.getInstance()
            ovulationDate.set(year, month, dayOfMonth)
            ovulationDate.add(Calendar.DAY_OF_MONTH, 14)

            // Period date
            val periodDate = Calendar.getInstance()
            periodDate.set(year, month, dayOfMonth)
            periodDate.add(Calendar.DAY_OF_MONTH, 28)

            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

            binding.ovulation.text = dateFormat.format(ovulationDate.time)
            binding.period.text = dateFormat.format(periodDate.time)

        }

        binding.back.setOnClickListener {
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}