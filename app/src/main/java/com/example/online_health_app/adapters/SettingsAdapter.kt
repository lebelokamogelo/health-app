package com.example.online_health_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.online_health_app.R
import com.example.online_health_app.data.Category
import com.example.online_health_app.data.Item

class SettingsAdapter(private val itemList: List<Item>) :
    RecyclerView.Adapter<SettingsAdapter.MyViewHolder>() {

    private var clickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: Item)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.setting_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.setting_recycle_item, parent, false)

        return MyViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        holder.text.text = item.text
        holder.itemView.setOnClickListener {
            // Notify the click listener when a category is clicked
            clickListener?.onItemClick(item)
        }
    }
}