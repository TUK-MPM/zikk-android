package com.example.zikk.model

import com.example.zikk.enum.IllegalParkingLocation
import com.example.zikk.enum.Status

data class Report(
    val reportId: String,
//    TODO
//    status, where 둘 다 ENUM 으로 변경 할 것
    val where: String,
    val status: String,
    val createdAt: String,
//    신고 내역 상세 조회에서 사용됨
    val address: String? = null,
    val mediaUrls: List<String>? = null,
    val reporterContact: String? = null,
)

