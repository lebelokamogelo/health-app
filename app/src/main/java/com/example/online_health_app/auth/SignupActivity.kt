package com.example.online_health_app.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.online_health_app.MainActivity
import com.example.online_health_app.data.User
import com.example.online_health_app.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.signup.setOnClickListener {

            binding.loading.visibility = View.VISIBLE
            binding.signup.visibility = View.GONE

            val name = binding.name.text?.trim().toString()
            val email = binding.email.text?.trim().toString()
            val phone = binding.phone.text?.trim().toString()
            val password = binding.password.text?.trim().toString()
            val confirm = binding.confirm.text?.trim().toString()

            val phoneNumberCode = "+27" + phone.replaceFirst("0", "")

            when {
                name.isEmpty() ->
                    binding.nameLayout.error =
                        "The name must not be empty and be 3 characters long."

                !isEmailValid(email) ->
                    binding.emailLayout.error = "The email address is invalid."

                phone.length < 10 ->
                    binding.phoneLayout.error = "The phone number is invalid."

                password.length < 8 ->
                    binding.passwordLayout.error =
                        "The password must be at least 8 characters long."

                confirm != password ->
                    binding.confirmLayout.error = "The passwords do not match."

                else -> {

                    // Create user with email and password
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser

                                // Create a SharedPreferences instance
                                val sharedPreferences =
                                    this.getSharedPreferences("user", Context.MODE_PRIVATE)

                                // Save the user's data to SharedPreferences
                                sharedPreferences.edit().putString("name", name).apply()
                                sharedPreferences.edit().putString("email", email).apply()
                                sharedPreferences.edit().putString("phone", phone).apply()
                                sharedPreferences.edit().putString("image", "").apply()
                                sharedPreferences.edit().putString("uuid", user?.uid.toString())
                                    .apply()

                                val db = FirebaseFirestore.getInstance()

                                val data = User(
                                    uuid = user?.uid.toString(),
                                    name = name,
                                    email = user?.email.toString(),
                                    phone = phoneNumberCode,
                                    image = ""
                                )

                                db.collection("patients")
                                    .document(user?.uid.toString())
                                    .set(data)
                                    .addOnSuccessListener {

                                        binding.loading.visibility = View.GONE
                                        binding.signup.visibility = View.VISIBLE

                                        Toasty.success(
                                            this,
                                            "Account created successfully",
                                            Toast.LENGTH_SHORT,
                                            true
                                        )
                                            .show()

                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener { _ ->

                                        binding.loading.visibility = View.GONE
                                        binding.signup.visibility = View.VISIBLE

                                        Toasty.info(
                                            this,
                                            "Something went wrong",
                                            Toast.LENGTH_SHORT,
                                            true
                                        ).show()
                                    }


                            } else {

                                binding.loading.visibility = View.GONE
                                binding.signup.visibility = View.VISIBLE

                                Toasty.info(
                                    this,
                                    "${task.exception?.message}",
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()

                            }
                        }
                }
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val pattern = Pattern.compile("^[a-zA-Z0-9.+_-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}