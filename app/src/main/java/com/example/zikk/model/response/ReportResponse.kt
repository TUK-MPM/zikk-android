package com.example.zikk.model.response

data class ReportResponse(
    val reportId: Long,
    val message: String,
    val reason: String?,
    val status: String,
    val createdAt: String,
    val repliedAt: String?
)
