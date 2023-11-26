package com.example.online_health_app.data

data class AvailableDoctor(
    val name: String,
    val specializing: String,
    val rating: Int,
    val experience: Int,
    val image: String?,
    val uuid: String,

    ) {
    constructor() : this("", "", 0, 0, "", "")
}