package com.example.online_health_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.online_health_app.databinding.FragmentBodyMassResultBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BodyMassResultFragment(private val value: Int) :
    BottomSheetDialogFragment() {

    private var _binding: FragmentBodyMassResultBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBodyMassResultBinding.inflate(inflater, container, false)

        val status = when {
            value < 18.5 -> "underweight"
            value >= 18.5 && value <= 24.9 -> "normal"
            value >= 25 && value <= 29.9 -> "overweight"
            else -> "obese"
        }

        val tips = when (status) {
            "underweight" ->
                "This means that you’re underweight, an indication that you do not have enough body fat to maintain good health. Being underweight can still have negative health consequences in males, such as hormonal imbalances, and decreased muscle mass."

            "normal" -> "Good news, it looks like you have the ideal amount of body fat. This is good for maintaining good physical, mental and emotional health. It means you are at a much lower risk of chronic diseases such as high blood pressure, heart disease, and Type 2 Diabetes."
            "overweight" -> "There is a mismatch between your height and your weight, meaning you're overweight. Your risk of developing a chronic disease is therefore higher. A few changes in lifestyle and diet could easily make a positive difference in achieving better health."
            else -> "Your BMI score states that you're obese. This means that your risk of developing chronic diseases and shortening your lifespan is much higher. On the bright side, losing weight can cut your level of risk down significantly."
        }

        binding.tips.text = tips
        binding.progress.progress = value.toFloat()
        binding.progress.text = value.toString()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}