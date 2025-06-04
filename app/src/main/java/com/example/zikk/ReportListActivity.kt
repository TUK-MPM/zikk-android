package com.example.zikk

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zikk.adapter.ReportAdapter
import com.example.zikk.databinding.ActivityReportListBinding
import com.example.zikk.model.Report
import com.example.zikk.model.response.ReportResponse
import com.example.zikk.util.PaginationUiUtils
import com.example.zikk.util.PaginationUtils
import com.example.zikk.util.PopupUtils

class ReportListActivity : BaseActivity() {

    val testList = ReportResponse(
        content = listOf(
            Report("rep_20250601_001", "DOT_BLOCK", "PROCESSING", "2025-06-01T10:30:00"),
            Report("rep_20250601_002", "PROTECTED_ZONE", "COMPLETED", "2025-06-01T09:15:00"),
            Report("rep_20250531_003", "DOT_BLOCK", "REJECTED", "2025-05-31T16:45:00"),
            Report("rep_20250530_004", "PROTECTED_ZONE", "PROCESSING", "2025-05-30T08:20:00"),
            Report("rep_20250529_005", "DOT_BLOCK", "COMPLETED", "2025-05-29T14:00:00"),
            Report("rep_20250528_006", "PROTECTED_ZONE", "REJECTED", "2025-05-28T13:10:00"),
            Report("rep_20250527_007", "DOT_BLOCK", "PROCESSING", "2025-05-27T12:25:00"),
            Report("rep_20250526_008", "PROTECTED_ZONE", "COMPLETED", "2025-05-26T11:40:00"),
            Report("rep_20250525_009", "DOT_BLOCK", "REJECTED", "2025-05-25T17:55:00"),
            Report("rep_20250524_010", "PROTECTED_ZONE", "PROCESSING", "2025-05-24T07:45:00"),
            Report("rep_20250523_011", "DOT_BLOCK", "COMPLETED", "2025-05-23T15:10:00"),
            Report("rep_20250522_012", "PROTECTED_ZONE", "REJECTED", "2025-05-22T08:30:00"),
            Report("rep_20250521_013", "DOT_BLOCK", "PROCESSING", "2025-05-21T16:15:00"),
            Report("rep_20250520_014", "PROTECTED_ZONE", "COMPLETED", "2025-05-20T10:00:00"),
            Report("rep_20250519_015", "DOT_BLOCK", "REJECTED", "2025-05-19T09:50:00"),
            Report("rep_20250518_016", "PROTECTED_ZONE", "PROCESSING", "2025-05-18T14:35:00"),
            Report("rep_20250517_017", "DOT_BLOCK", "COMPLETED", "2025-05-17T11:25:00"),
            Report("rep_20250516_018", "PROTECTED_ZONE", "REJECTED", "2025-05-16T13:45:00"),
            Report("rep_20250515_019", "DOT_BLOCK", "PROCESSING", "2025-05-15T12:00:00"),
            Report("rep_20250514_020", "PROTECTED_ZONE", "COMPLETED", "2025-05-14T08:10:00")
        ),
        totalPages = 4,
        hasNext = true,
        hasPrevious = false,
        isFirst = true,
        isLast = false
    )

    private lateinit var binding: ActivityReportListBinding
    private var currentPage = 1     // 현재 페이지 번호
    private var displayedList: List<Report> = testList.content // 필터링 및 정렬된 결과 리스트
    private var currentFilter: String? = null
    private var currentSortDescending: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentViewWithBinding(ActivityReportListBinding::inflate)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 최초 실행 시 applyFilterAndSort()로 데이터 필터링 & 정렬 후 loadPage(1) 호출
        binding.reportRecyclerView.layoutManager = LinearLayoutManager(this)
        applyFilterAndSort()

        binding.ivBack.setOnClickListener { finish() }
        binding.btnFilter.setOnClickListener { showCustomPopup(it) }
        binding.btnSortStatus.setOnClickListener { showSortPopup(it) }
    }

    // 상태와 정렬 기준에 동시에 맞게 페이지 로딩
    private fun applyFilterAndSort() {
        val filtered = testList.content.filter { currentFilter == null || it.status == currentFilter }
        displayedList = PaginationUtils.sortByDate(filtered, currentSortDescending)
        loadPage(1)
    }

    // 정렬 버튼 누를 때 마다 페이지 새로고침
    private fun loadPage(page: Int) {
        currentPage = page
        val pageSize = 5
        val pageList = PaginationUtils.paginate(displayedList, page, pageSize)

        binding.reportRecyclerView.adapter = ReportAdapter(pageList) { report ->
            val intent = Intent(this, ReportDetailActivity::class.java)
            intent.putExtra("status", report.status)
            startActivity(intent)
        }
        val totalPages = PaginationUtils.getTotalPages(displayedList.size, pageSize)

        PaginationUiUtils.setupPagination(
            context = this,
            container = binding.paginationLayout,
            currentPage = page,
            totalPages = totalPages,
            onPageClick = { selectedPage -> loadPage(selectedPage) }
        )
    }
    // 정렬 버튼 팝업 띄우기
    private fun showSortPopup(anchor: View) {
        PopupUtils.showSortPopup(this, anchor) { isDescending ->
            currentSortDescending = isDescending
            binding.btnSortStatus.text = if (isDescending) "최신순으로 나열" else "오래된 순으로 나열"
            applyFilterAndSort()
        }
    }

    // 상태 필터 버튼 팝업 띄우기
    private fun showCustomPopup(anchor: View) {
        PopupUtils.showFilterPopup(this, anchor) { filter ->
            currentFilter = filter
            binding.btnFilter.text = when (filter) {
                null -> "전체 보기"
                "COMPLETED" -> "승인만 보기"
                "PROCESSING" -> "처리중만 보기"
                "REJECTED" -> "반려만 보기"
                else -> "전체 보기"
            }
            applyFilterAndSort()
        }
    }
}
