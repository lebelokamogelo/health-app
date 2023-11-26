package com.example.online_health_app.screens

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.online_health_app.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import es.dmoral.toasty.Toasty

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        binding.update.setOnClickListener {

            binding.loading.visibility = View.VISIBLE
            binding.update.visibility = View.GONE

            val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

            val email = sharedPreferences.getString("email", "")
            val current = binding.currentPassword.text
            val newPassword = binding.newPassword.text?.trim()
            val confirmPassword = binding.confirmPassword.text?.trim()

            val user = FirebaseAuth.getInstance().currentUser
            val credential = EmailAuthProvider.getCredential(email.toString(), current.toString())

            user?.reauthenticate(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        if (newPassword.toString() == confirmPassword.toString() && newPassword?.length!! >= 8) {
                            user.updatePassword(newPassword.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Password has been updated successfully.
                                        Toasty.success(
                                            this,
                                            "Password has been updated successfully."
                                        ).show()
                                        binding.loading.visibility = View.GONE
                                        binding.update.visibility = View.VISIBLE

                                    } else {
                                        // Password update failed, handle the error.
                                        Toasty.info(this, "Failed to update password").show()
                                        binding.loading.visibility = View.GONE
                                        binding.update.visibility = View.VISIBLE
                                    }
                                }
                        } else {
                            Toasty.info(this, "Password must be 8 chars or does not match").show()
                            binding.loading.visibility = View.GONE
                            binding.update.visibility = View.VISIBLE
                        }

                    } else {
                        // Authentication failed, handle the error.
                        val exception = task.exception
                        // Handle the exception appropriately (e.g., show an error message to the user).
                        Toasty.info(this, exception?.message.toString()).show()
                        binding.loading.visibility = View.GONE
                        binding.update.visibility = View.VISIBLE
                    }
                }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}