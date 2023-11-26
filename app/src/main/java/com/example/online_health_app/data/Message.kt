package com.example.online_health_app.data

data class Message(
    val receiver: String,
    val sender: String,
    val text: String,
    val time: String
) {
    constructor() : this("", "", "", "")
}