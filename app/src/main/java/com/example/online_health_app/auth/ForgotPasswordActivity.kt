package com.example.online_health_app.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.online_health_app.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.send.setOnClickListener {

            binding.loading.visibility = View.VISIBLE
            binding.send.visibility = View.GONE

            val email = binding.email.text?.trim().toString()

            try {
                // Send the password reset email
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            binding.loading.visibility = View.GONE
                            binding.send.visibility = View.VISIBLE

                            // The password reset email was sent
                            Toasty.success(
                                this,
                                "Password reset email sent ${task.result}",
                                Toast.LENGTH_SHORT,
                                true
                            )
                                .show()

                            binding.email.text = null
                        } else {

                            binding.loading.visibility = View.GONE
                            binding.send.visibility = View.VISIBLE

                            // Document does not exist
                            Toasty.info(
                                this,
                                "Failed to send password reset email",
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
            } catch (e: Exception) {

                binding.loading.visibility = View.GONE
                binding.send.visibility = View.VISIBLE

                // Handle the exception
                Toasty.info(
                    this,
                    "An error occurred. ${e.message}",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        }

        binding.back.setOnClickListener {

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}