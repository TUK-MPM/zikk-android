package com.example.zikk.util

import com.example.zikk.model.Report
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PaginationUtils {

    // 날짜 문자열 파싱용 포맷터 (예: "2025-05-26T15:30:00")
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // 생성일 기준 정렬 (true: 최신순, false: 오래된순)
    fun sortByDate(list: List<Report>, descending: Boolean = true): List<Report> {
        return if (descending) {
            list.sortedByDescending { LocalDateTime.parse(it.createdAt, formatter) }
        } else {
            list.sortedBy { LocalDateTime.parse(it.createdAt, formatter) }
        }
    }

    // 페이지 번호와 페이지 크기에 따라 하위 리스트 반환
    fun paginate(list: List<Report>, page: Int, pageSize: Int): List<Report> {
        val fromIndex = (page - 1) * pageSize
        val toIndex = (fromIndex + pageSize).coerceAtMost(list.size)
        return if (fromIndex in 0 until list.size) list.subList(fromIndex, toIndex) else emptyList()
    }

    // 전체 페이지 수 계산 (올림 처리)
    fun getTotalPages(listSize: Int, pageSize: Int): Int {
        return (listSize + pageSize - 1) / pageSize
    }

    // 현재 페이지 기준으로 보여줄 페이지 번호 범위 계산 (예: [1, 2, 3, 4, 5])
    fun getPageRange(currentPage: Int, totalPages: Int, range: Int = 2): List<Int> {
        val startPage = (currentPage - range).coerceAtLeast(1)
        val endPage = (currentPage + range).coerceAtMost(totalPages)
        return (startPage..endPage).toList()
    }
}
