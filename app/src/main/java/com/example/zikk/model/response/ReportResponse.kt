package com.example.zikk.model.response

import com.example.zikk.model.Report

data class ReportResponse(
    val content: List<Report>,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
    val isFirst: Boolean,
    val isLast: Boolean
)

