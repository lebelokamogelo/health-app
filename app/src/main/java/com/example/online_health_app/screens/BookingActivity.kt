package com.example.online_health_app.screens

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.online_health_app.R
import com.example.online_health_app.data.Appointment
import com.example.online_health_app.databinding.ActivityBookingBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class BookingActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityBookingBinding
    private var selectedSlot: RadioButton? = null
    private var selectedTime: String? = null
    private var selectedCalendarDate: String? = null
    private var id: String? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val doctorId = intent.getStringExtra("uuid")
        val name = intent.getStringExtra("name")

        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        userId = sharedPreferences.getString("uuid", "")

        val calendarView: CalendarView = findViewById(R.id.calendarView)

        val minDate = Calendar.getInstance()
        minDate.add(Calendar.DAY_OF_MONTH, 3)
        calendarView.minDate = minDate.timeInMillis

        selectedTime = ""

        binding.back.setOnClickListener {
            finish()
        }

        selectedCalendarDate =
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(minDate.time)

        binding.book.setOnClickListener {
            binding.book.visibility = View.GONE
            binding.loading.visibility = View.VISIBLE

            if (selectedTime!!.isEmpty()) {
                Toasty.info(
                    this,
                    "Please choose a slot",
                    Toast.LENGTH_SHORT,
                    true
                ).show()

                binding.book.visibility = View.VISIBLE
                binding.loading.visibility = View.GONE

            } else {

                val userAppointmentCollection = FirebaseFirestore.getInstance()
                    .collection("appointments")
                    .document(userId.toString())
                    .collection("appointment")

                val query = userAppointmentCollection.whereEqualTo("date", selectedCalendarDate)

                query.get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            // No existing appointment on the selected date, proceed with creating a new one

                            id = UUID.randomUUID().toString()
                            val data = Appointment(
                                selectedCalendarDate.toString(),
                                selectedTime.toString(),
                                doctorId.toString(),
                                "Mankweng",
                                "Pending",
                                userId.toString(),
                                name.toString(),
                                id
                            )

                            FirebaseFirestore.getInstance()
                                .collection("appointments")
                                .document(userId.toString())
                                .collection("appointment")
                                .document(id.toString())
                                .set(data)
                                .addOnSuccessListener {
                                    makePayment(id.toString(), userId.toString())
                                }
                                .addOnFailureListener {
                                    binding.book.visibility = View.VISIBLE
                                    binding.loading.visibility = View.GONE

                                    Toasty.warning(
                                        this,
                                        "Something went wrong. Try again",
                                        Toast.LENGTH_SHORT,
                                        true
                                    ).show()
                                }
                        } else {
                            // User already has an appointment on the selected date, show a message
                            binding.book.visibility = View.VISIBLE
                            binding.loading.visibility = View.GONE

                            Toasty.warning(
                                this,
                                "You already have an appointment on this date",
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        // Handle failure
                        binding.book.visibility = View.VISIBLE
                        binding.loading.visibility = View.GONE

                        Toasty.warning(
                            this,
                            "Something went wrong. Try again",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
            }

        }

        // Set up the calendar view
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val formattedDate =
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selectedDate.time)

            selectedCalendarDate = formattedDate

        }

        binding.morningSlotsRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
            updateSlotSelection(selectedRadioButton)
        }

        binding.afternoonSlotsRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
            updateSlotSelection(selectedRadioButton)
        }

    }

    private fun updateSlotSelection(selectedRadioButton: RadioButton?) {
        // Clear the previous selection
        selectedSlot?.isChecked = false
        // Update the current selection
        selectedSlot = selectedRadioButton

        selectedTime = selectedRadioButton?.text.toString()
    }

    private fun makePayment(id: String, userId: String) {
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name", "Appointment")
            options.put(
                "description",
                "You are being billed for a standard appointment. The appointment charge is R200"
            )
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", "20000")

            val prefill = JSONObject()
            prefill.put("email", "lebelokamogelo47@gmail.com")
            prefill.put("contact", "+27721789611")

            options.put("prefill", prefill)
            co.open(this, options)
        } catch (e: Exception) {

            val userAppointmentCollection = FirebaseFirestore.getInstance()
                .collection("appointments")
                .document(userId)
                .collection("appointment")

            userAppointmentCollection.document(id)
                .delete()
                .addOnSuccessListener {

                    Toasty.info(
                        this,
                        "Payment was cancelled",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onPaymentSuccess(p0: String?) {
        binding.book.visibility = View.VISIBLE
        binding.loading.visibility = View.GONE

        val instance = FirebaseFirestore.getInstance()

        val docRef = instance.collection("sales").document("BNqAM4fXY8PeBAGHjUj2")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val amount = Integer.parseInt(document.data?.get("amount").toString()) + 200
                    
                    docRef
                        .update("amount", amount.toString())
                }
            }

        Toasty.success(
            this,
            "Appointment booked",
            Toast.LENGTH_SHORT,
            true
        ).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        binding.book.visibility = View.VISIBLE
        binding.loading.visibility = View.GONE

        val userAppointmentCollection = FirebaseFirestore.getInstance()
            .collection("appointments")
            .document(userId.toString())
            .collection("appointment")

        userAppointmentCollection.document(id.toString())
            .delete()
            .addOnSuccessListener {

                Toasty.info(
                    this,
                    "Payment was not successful",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
    }
}