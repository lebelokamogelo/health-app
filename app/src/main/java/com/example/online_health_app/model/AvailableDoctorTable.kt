package com.example.online_health_app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "available_doctors")
data class AvailableDoctorTable(
    val name: String,
    val specializing: String,
    val rating: Int,
    val experience: Int,
    val image: String?,
    @PrimaryKey
    val uuid: String
)