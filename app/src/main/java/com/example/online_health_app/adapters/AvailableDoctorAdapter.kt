package com.example.online_health_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.online_health_app.R
import com.example.online_health_app.data.AvailableDoctor

class AvailableDoctorAdapter(
    private val availableDoctorList: MutableList<AvailableDoctor>
) : RecyclerView.Adapter<AvailableDoctorAdapter.AvailableDoctorViewHolder>() {

    inner class AvailableDoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.doctorImage)
        val text: TextView = itemView.findViewById(R.id.textViewName)
        val specialize: TextView = itemView.findViewById(R.id.textViewSpecializing)
        val rate: RatingBar = itemView.findViewById(R.id.ratingBar)
        val experience: TextView = itemView.findViewById(R.id.textViewExperienceCount)

        init {
            // Set click listener on the item view to handle item clicks
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val doctor = availableDoctorList[position]
                    itemListener?.onItemClick(doctor)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(doctor: AvailableDoctor)
    }

    private var itemListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableDoctorViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.available_doctor, parent, false)
        return AvailableDoctorViewHolder(view)
    }

    override fun getItemCount(): Int {
        return availableDoctorList.size
    }

    override fun onBindViewHolder(holder: AvailableDoctorViewHolder, position: Int) {
        val doctor = availableDoctorList[position]
        holder.text.text = doctor.name
        holder.specialize.text = doctor.specializing
        holder.rate.rating = doctor.rating.toFloat()
        holder.experience.text = buildString {
            append(doctor.experience)
            append(" Years")
        }
        //holder.image.setImageResource(doctor.image)
    }
}
