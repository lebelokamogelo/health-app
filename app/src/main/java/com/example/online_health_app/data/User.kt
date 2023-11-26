package com.example.online_health_app.data

data class User(
    val uuid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val image: String? = ""
)