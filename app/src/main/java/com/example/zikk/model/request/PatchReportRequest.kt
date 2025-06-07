package com.example.zikk.model.request

data class PatchReportRequest(
    val phone: String,
    val address: String,
    val status: String,
    val imageUrls: List<String>
)
