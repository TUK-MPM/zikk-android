package com.example.zikk.enum

enum class Status(val description: String) {
    REPORTED("접수"),
    PROCESSING("검토중"),
    COMPLETED("완료");

    override fun toString(): String {
        return description
    }
}
