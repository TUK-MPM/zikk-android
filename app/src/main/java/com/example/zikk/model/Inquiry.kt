package com.example.zikk.model

data class Inquiry(
    val inquiryId: String,
    val title: String,
    val status: String,      // "PENDING", "COMPLETED"
    val createdAt: String    // ISO 8601 문자열
)
