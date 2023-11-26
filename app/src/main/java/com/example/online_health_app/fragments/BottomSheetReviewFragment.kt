package com.example.online_health_app.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.online_health_app.data.Reviews
import com.example.online_health_app.databinding.FragmentBottomSheetReviewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BottomSheetReviewFragment(private val uuid: String) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetReviewBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetReviewBinding.inflate(inflater, container, false)


        var rating = 1.0F

        val uuid = uuid
        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)

        val name = sharedPreferences?.getString("name", "")

        binding.ratingBarReview.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                // The user has released their touch on the RatingBar
                rating = binding.ratingBarReview.rating
                Log.d("Test", rating.toString())
            }
            false
        }


        binding.submit.setOnClickListener {
            val message = binding.message.text?.trim()
            rating = binding.ratingBarReview.rating

            if (message != null) {
                if (message.isEmpty()) {
                    binding.messageLayout.requestFocus()
                } else {

                    val db = FirebaseFirestore.getInstance()
                    val reviewsRef = db.collection("reviews")

                    reviewsRef.document(uuid).collection("review").add(
                        Reviews(
                            name.toString(),
                            rating.toString(),
                            message.toString(),
                            LocalDate.now().format(
                                DateTimeFormatter.ofPattern("dd MMM yyyy")
                            ).toString()
                        )
                    )
                        .addOnSuccessListener {

                            dismiss()

                            // Message added successfully
                            binding.message.text?.clear() // Clear the EditText
                            Toasty.success(requireContext(), "Thanks for reviewing", Toast.LENGTH_SHORT)
                                .show()

                        }
                        .addOnFailureListener { _ ->
                            // Error adding message
                            //Log.e(TAG, "Error adding message", e)

                            Toasty.info(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }

                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
