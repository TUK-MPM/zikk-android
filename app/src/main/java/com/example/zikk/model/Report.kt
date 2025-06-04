package com.example.zikk.model

data class Report(
    val reportId: String,
    val where: IllegalParkingLocation,
    val address: String,
    val mediaUrls: List<String>,
    val reporterContact: String,
    val status: ReportStatus,
    val createdAt: String
)

enum class IllegalParkingLocation(val description: String) {
    DOT_BLOCK("점자블록"),
    TRAFFIC_ISLAND("교통섬 내부"),
    PROTECTED_ZONE("보호구역"),
    WALKWAY_OTHER("상기 사항 외 보도"),
    OTHER("기타");

    override fun toString(): String {
        return description
    }
}

enum class ReportStatus(val description: String) {
    REPORTED("접수"),
    PROCESSING("검토중"),
    COMPLETED("완료");

    override fun toString(): String {
        return description
    }
}
