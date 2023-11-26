package com.example.online_health_app.screens

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.online_health_app.databinding.ActivityProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val db = FirebaseFirestore.getInstance()

        binding.fullName.text = sharedPreferences.getString("name", "")
        binding.nameEditText.setText(sharedPreferences.getString("name", ""))
        binding.emailEditText.setText(sharedPreferences.getString("email", ""))
        binding.phoneNumberEditText.setText(sharedPreferences.getString("phone", ""))
        val uuid = sharedPreferences.getString("uuid", "")

        binding.back.setOnClickListener {
            finish()
        }

        binding.save.setOnClickListener {

            val userRef = db.collection("patients").document(uuid.toString())

            val updates = hashMapOf<String, Any>(
                "name" to binding.nameEditText.text.toString(),
                "phone" to binding.phoneNumberEditText.text.toString(),
            )

            userRef
                .update(updates)
                .addOnSuccessListener {
                    val sharedPreferences =
                        this.getSharedPreferences("user", Context.MODE_PRIVATE)

                    // Save the user's data to SharedPreferences
                    sharedPreferences.edit().putString("name", binding.nameEditText.text.toString())
                        .apply()
                    sharedPreferences.edit()
                        .putString("phone", binding.phoneNumberEditText.text.toString())
                        .apply()

                    Toasty.success(this@ProfileActivity, "Successfully updated.").show()
                }
                .addOnFailureListener {
                    Toasty.error(this@ProfileActivity, "Failed to update").show()
                }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}