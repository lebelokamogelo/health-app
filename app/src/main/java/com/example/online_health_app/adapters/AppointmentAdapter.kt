package com.example.online_health_app.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.online_health_app.R
import com.example.online_health_app.data.Appointment
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty

class AppointmentAdapter(private var appointments: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
        val timeTextView: TextView = itemView.findViewById(R.id.textViewTime)
        val doctorNameTextView: TextView = itemView.findViewById(R.id.textViewDoctorName)
        val placeTextView: TextView = itemView.findViewById(R.id.textViewPlace)
        val statusTextView: TextView = itemView.findViewById(R.id.textViewStatus)
        private val cancelButton: Button = itemView.findViewById(R.id.buttonCancel)

        init {
            // Set click listener on the item view to handle item clicks
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val appointment = appointments[position]
                    itemListener?.onItemClick(appointment)
                }
            }

            // Set click listener on the cancel button (if needed)
            cancelButton.setOnClickListener {
                // Implement your cancellation logic here

                val id = appointments[adapterPosition].uuid

                AlertDialog.Builder(this.itemView.context)
                    .setMessage("Are you sure you want to delete?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                        val db = FirebaseFirestore.getInstance()

                        val appointmentCollectionRef = db.collection("appointments")
                            .document(id)
                            .collection("appointment")

                        appointmentCollectionRef.whereEqualTo("id", appointments[adapterPosition].id)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                for (document in querySnapshot.documents) {
                                    document.reference.delete()
                                }

                                appointments = appointments.filter { data -> appointments[adapterPosition].id != data.id }
                                notifyDataSetChanged()

                                Toasty.success(
                                    this.itemView.context,
                                    "Successfully deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                // Handle deletion failure
                                Toasty.info(
                                    this.itemView.context,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    .setNegativeButton("No", null)
                    .show()

            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(appointment: Appointment)
    }

    private var itemListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.appointment_card, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("size", appointments.size.toString())
        return appointments.size
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]

        holder.dateTextView.text = appointment.date
        holder.timeTextView.text = appointment.time
        holder.doctorNameTextView.text = appointment.name
        holder.placeTextView.text = appointment.place
        holder.statusTextView.text = appointment.status

    }
}
