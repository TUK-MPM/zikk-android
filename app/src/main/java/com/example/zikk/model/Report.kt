package com.example.zikk.model

import com.example.zikk.enum.IllegalParkingLocation
import com.example.zikk.enum.Status

data class Report(
    val reportId: String,
    val where: IllegalParkingLocation,
    val address: String,
    val mediaUrls: List<String>,
    val reporterContact: String,
    val status: Status,
    val createdAt: String
)

