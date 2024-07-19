package com.example.recyclerviewapp
data class Trip(
    val from: String,
    val to: String,
    val status: TripStatus,
    val distance: String,
    val duration: String,
    val date: String,
    val stations: String
)
