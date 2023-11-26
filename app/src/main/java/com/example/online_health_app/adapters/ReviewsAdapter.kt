package com.example.online_health_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.online_health_app.R
import com.example.online_health_app.data.Reviews

class ReviewsAdapter(private val reviews: List<Reviews>) :
    RecyclerView.Adapter<ReviewsAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.ReviewName)
        val rating: RatingBar = itemView.findViewById(R.id.ratingBarReview)
        val time: TextView = itemView.findViewById(R.id.ReviewTime)
        val text: TextView = itemView.findViewById(R.id.ReviewText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.review_layout_item, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = reviews[position]

        holder.name.text = review.name
        holder.rating.rating = review.rating.toFloat()
        holder.time.text = review.time
        holder.text.text = review.text
    }
}