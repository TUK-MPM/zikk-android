package com.example.zikk.model

import com.example.zikk.enum.IllegalParkingLocation
import com.example.zikk.enum.Status

data class Report(
    val reportId: String,
    val where: String,
    val status: String,
    val createdAt: String,
    val address: String
)