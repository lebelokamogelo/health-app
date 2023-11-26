package com.example.online_health_app.data

import com.example.online_health_app.R

data class Notification(
    val name: String,
    val message: String,
    val time: String,
    val icon: Int?
) {
    constructor() : this("", "", "", R.drawable.bell_45)
}
