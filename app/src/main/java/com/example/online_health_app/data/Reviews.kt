package com.example.online_health_app.data

data class Reviews(
    val name: String,
    val rating: String,
    val text: String,
    val time: String
){
    constructor(): this("", "", "", "")
}
