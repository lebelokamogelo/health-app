package com.example.online_health_app.screens

import android.R.attr.text
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.example.online_health_app.databinding.ActivityBreatheBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BreatheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBreatheBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBreatheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        var animatorSet = AnimatorSet()

        var status = 0
        var current = 0

        val sharedPreferences = getSharedPreferences("Breathing", Context.MODE_PRIVATE)

        val lastTime = sharedPreferences.getString("lastTime", "")
        val breathe = sharedPreferences.getString("breathe", "")
        val duration = sharedPreferences.getString("duration", "")

        binding.lastTime.text = "Last Time at $lastTime"
        binding.breaths.text = "$breathe Breaths"
        binding.duration.text = "$duration minutes"

        binding.start.setOnClickListener {

            when (status) {
                0 -> {
                    status = 1
                    binding.breathing.text = "Inhale and Exhale"
                    binding.start.text = "Stop"
                    animatorSet.playTogether(
                        ObjectAnimator.ofFloat(binding.image, "scaleX", 1f, 0.5f, 1f),
                        ObjectAnimator.ofFloat(binding.image, "scaleY", 1f, 0.5f, 1f),
                        ObjectAnimator.ofFloat(text, "translationX", -200f, 0f),
                        ObjectAnimator.ofFloat(binding.image, "rotation", 0f, 360f),
                    )
                    animatorSet.interpolator = AccelerateInterpolator()
                    animatorSet.duration = 4000
                    animatorSet.childAnimations.forEach {
                        val animator = it as ObjectAnimator
                        animator.repeatCount = ObjectAnimator.INFINITE
                        animator.repeatMode = ObjectAnimator.RESTART
                        animator.addUpdateListener {
                            if (status == 1) {
                                current++
                                binding.breaths.text = (current / 960).toString() + " Breaths"
                                binding.countTime.text = (current / 250).toString() + " seconds"
                            }
                        }
                    }
                    animatorSet.doOnEnd {
                        binding.breathing.text = "Good Job"
                        status = 0
                        binding.start.text = "Start"
                        animatorSet = AnimatorSet()

                        val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                            Calendar.getInstance().time
                        )

                        binding.lastTime.text = "Last Time at $formattedTime"
                        binding.duration.text = String.format("%.2f minutes", current / 15000.0)

                        val sharedPreferences =
                            getSharedPreferences("Breathing", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                        editor.putString("lastTime", formattedTime)
                        editor.putString("breathe", (current / 960).toString())
                        editor.putString(
                            "duration",
                            String.format("%.2f", current / 15000.0)
                        )

                        editor.apply()
                    }
                    animatorSet.start()
                }

                1 -> {
                    status = 0
                    binding.start.text = "Start"
                    binding.breathing.text = null
                    animatorSet.cancel()
                    current = 0
                    animatorSet = AnimatorSet()
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}