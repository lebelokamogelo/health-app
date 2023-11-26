package com.example.online_health_app.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.online_health_app.databinding.ActivityBodyMassBinding
import com.example.online_health_app.fragments.BodyMassResultFragment
import kotlin.math.roundToInt

class BodyMassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBodyMassBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBodyMassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var height = 140F
        binding.numberPickerHeight.setOnValueChangedListener { _, _, newVal ->
            height = newVal.toFloat()

        }

        var weight = 45.0F
        binding.numberPickerWeight.setOnValueChangedListener { _, _, newVal ->
            weight = newVal.toFloat()

        }

        binding.back.setOnClickListener {
            finish()
        }


        binding.calculate.setOnClickListener {

            val value: Int = (weight / (height / 100 * height / 100)).roundToInt()
            BodyMassResultFragment(value).show(
                supportFragmentManager,
                "BottomSheetTag"
            )
        }
    }
}