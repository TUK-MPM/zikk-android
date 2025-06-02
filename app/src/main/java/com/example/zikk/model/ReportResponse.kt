package com.example.zikk.model

data class ReportResponse(
    val content: List<Report>,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
    val isFirst: Boolean,
    val isLast: Boolean
)

