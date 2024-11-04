package com.example.lunchmate.model


data class Message(
    var text: String = "",
    var senderId: String = "",
    var timestamp: Long = 0L
) {
    // Empty constructor required by Firebase
    constructor() : this("", "", 0L)
}