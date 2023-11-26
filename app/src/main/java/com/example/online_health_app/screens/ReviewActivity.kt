package com.example.online_health_app.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.online_health_app.adapters.ReviewsAdapter
import com.example.online_health_app.data.Reviews
import com.example.online_health_app.databinding.ActivityReviewBinding
import com.example.online_health_app.fragments.BottomSheetReviewFragment
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding

    private val reviewList: ArrayList<Reviews> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()
        val reviewsRef = db.collection("reviews")

        // Get the data from the Intent extras
        val uuid = intent.getStringExtra("uuid")


        binding.addReview.setOnClickListener {
            BottomSheetReviewFragment(uuid!!).show(supportFragmentManager, "BottomSheetTag")
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.loading.visibility = View.VISIBLE
        binding.reviewsRecycleView.visibility = View.GONE

        val recyclerView = binding.reviewsRecycleView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val reviewsAdapter = ReviewsAdapter(reviewList)
        recyclerView.adapter = reviewsAdapter

        reviewsRef.document(uuid.toString()).collection("review")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                for (document in snapshot!!.documentChanges) {
                    if (document.type == DocumentChange.Type.ADDED) {
                        val reviewData = document.document.toObject(Reviews::class.java)
                        reviewList.add(reviewData)
                        reviewsAdapter.notifyItemInserted(reviewList.size - 1)
                    }
                }

                binding.loading.visibility = View.GONE
                binding.reviewsRecycleView.visibility = View.VISIBLE
            }

    }
}