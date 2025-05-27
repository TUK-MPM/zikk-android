package com.example.zikk.model

data class Notice(val notiId: Int, val title: String, val content: String, val createdAt: String)

object NoticeDataSource {
    fun getExample(): List<Notice> {
        return listOf(
            Notice(1, "asdf", "asdfasdf", "2025-05-27 12:00:00"),
            Notice(2, "asdf", "asdfasdf", "2025-05-27 12:00:00"),
            Notice(3, "asdf", "asdfasdf", "2025-05-27 12:00:00"),
            Notice(4, "asdf", "asdfasdf", "2025-05-27 12:00:00"),
            Notice(5, "asdf", "asdfasdf", "2025-05-27 12:00:00"),
        )
    }
}