package com.example.online_health_app.data

import com.example.online_health_app.R

data class MessageItem(
    val name: String,
    val profileImage: Int,
    val lastMessage: String
) {
    constructor() : this("", R.id.doctorImage, "")
}
