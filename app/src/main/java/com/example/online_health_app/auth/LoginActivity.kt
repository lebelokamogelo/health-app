package com.example.online_health_app.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.online_health_app.MainActivity
import com.example.online_health_app.data.User
import com.example.online_health_app.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        val db = FirebaseFirestore.getInstance()
        auth = Firebase.auth

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.sign.setOnClickListener {

            val email = binding.email.text?.trim().toString()
            val password = binding.password.text?.trim().toString()

            binding.loading.visibility = View.VISIBLE
            binding.sign.visibility = View.GONE

            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            // Sign in success
                            val user = auth.currentUser

                            val userRef = db.collection("patients").document(user?.uid.toString())

                            userRef.get()
                                .addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                        val data = documentSnapshot.toObject(User::class.java)

                                        // Create a SharedPreferences instance
                                        val sharedPreferences =
                                            this.getSharedPreferences("user", Context.MODE_PRIVATE)

                                        // Save the user's data to SharedPreferences
                                        sharedPreferences.edit().putString("name", data?.name)
                                            .apply()
                                        sharedPreferences.edit().putString("email", user?.email)
                                            .apply()
                                        sharedPreferences.edit().putString("phone", data?.phone)
                                            .apply()
                                        sharedPreferences.edit().putString("image", data?.image)
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("uuid", user?.uid.toString())
                                            .apply()

                                        binding.loading.visibility = View.GONE
                                        binding.sign.visibility = View.VISIBLE

                                        Toasty.success(
                                            this,
                                            "Authenticated successfully",
                                            Toast.LENGTH_SHORT,
                                            true
                                        )
                                            .show()

                                        val trashCollectionRef = db.collection("trash")

                                        trashCollectionRef.whereEqualTo("email", email.lowercase())
                                            .get()
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    for (document in task.result!!) {

                                                        document.reference.delete()
                                                            .addOnSuccessListener {
                                                                Toasty.info(
                                                                    this,
                                                                    "Your account was restored successfully."
                                                                )
                                                                    .show()
                                                            }
                                                    }
                                                }
                                            }

                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()


                                    } else {
                                        binding.loading.visibility = View.GONE
                                        binding.sign.visibility = View.VISIBLE
                                        // Document does not exist
                                        Toasty.info(
                                            this,
                                            "Something went wrong",
                                            Toast.LENGTH_SHORT,
                                            true
                                        ).show()
                                    }
                                }
                                .addOnFailureListener { _ ->
                                    // TODO: Handle failed data retrieval

                                    binding.loading.visibility = View.GONE
                                    binding.sign.visibility = View.VISIBLE

                                    Toasty.info(
                                        this,
                                        "Failed to retrieve data",
                                        Toast.LENGTH_SHORT,
                                        true
                                    ).show()
                                }


                        } else {
                            // If sign in fails, display a message to the user.

                            binding.loading.visibility = View.GONE
                            binding.sign.visibility = View.VISIBLE

                            Toasty.info(
                                this,
                                "Incorrect username or password",
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
            } catch (e: Exception) {

                binding.loading.visibility = View.GONE
                binding.sign.visibility = View.VISIBLE

                // Handle errors
                Toasty.info(
                    this,
                    "Something went wrong.",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        }

        binding.forgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            finish()
        }

        binding.signup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }
}