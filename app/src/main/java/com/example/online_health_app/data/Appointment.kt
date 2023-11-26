package com.example.online_health_app.data

data class Appointment(
    val date: String,
    val time: String,
    val doctorId: String,
    val place: String,
    val status: String,
    val uuid: String,
    val name: String,
    val id: String?,
) {
    constructor() : this("", "", "", "", "", "", "", "")
}