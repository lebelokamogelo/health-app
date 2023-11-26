package com.example.online_health_app.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.online_health_app.databinding.ActivityDoctorDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore

class DoctorDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorDetailsBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDoctorDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Get the data from the Intent extras
        val doctorName = intent.getStringExtra("doctorName")
        val doctorSpecializing = intent.getStringExtra("doctorSpecializing")
        val doctorRating = intent.getIntExtra("doctorRating", 0)
        val doctorExperience = intent.getIntExtra("doctorExperience", 0)
        val doctorId = intent.getStringExtra("uuid")
        var name: String? = null

        val db = FirebaseFirestore.getInstance()
        val reviewsRef = db.collection("reviews")

        binding.back.setOnClickListener {
            finish()
        }

        reviewsRef.document(doctorId.toString()).collection("review")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                binding.textViewReviewCount.text = snapshot?.size().toString()

            }

        val docRef = FirebaseFirestore.getInstance().collection("doctors")
            .document(doctorId.toString())

        docRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        name = document.getString("name")
                        // Use the name value as needed
                    }
                }
            }

        //val doctorImage = intent.getIntExtra("doctorImage", 0)

        binding.doctor.text = doctorName
        binding.textViewSpecializing.text = doctorSpecializing
        binding.ratingBar.rating = doctorRating.toFloat()
        binding.textViewExperienceCount.text = "$doctorExperience years"
        //binding.image.setImageResource(doctorImage)

        binding.book.setOnClickListener {

            val intent = Intent(this, BookingActivity::class.java)
            intent.putExtra("doctorName", doctorName)
            intent.putExtra("uuid", doctorId)
            intent.putExtra("name", name)
            startActivity(intent)
        }

        binding.review.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra("uuid", doctorId)
            startActivity(intent)
        }
    }
}