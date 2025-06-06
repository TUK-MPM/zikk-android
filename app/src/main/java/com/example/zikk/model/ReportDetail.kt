package com.example.zikk.model

data class ReportDetail(
    val number: String,
    val reportId: String,
    val where: String,
    val address: String,
    val mediaUrls: List<String>,
    val status: String,
    val createdAt: String
)

