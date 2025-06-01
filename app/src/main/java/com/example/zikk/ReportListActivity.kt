package com.example.zikk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zikk.adapter.ReportAdapter
import com.example.zikk.databinding.ActivityReportListBinding
import com.example.zikk.model.Report
import com.example.zikk.util.PaginationUiUtils
import com.example.zikk.util.PaginationUtils

class ReportListActivity : BaseActivity() {

    private val testList = listOf(
        Report("rep_20250526_001", "DOT_BLOCK", "PROCESSING", "2025-05-26T15:30:00"),
        Report("rep_20250525_002", "PROTECTED_ZONE", "COMPLETED", "2025-05-25T11:20:00"),
        Report("rep_20250524_003", "DOT_BLOCK", "REJECTED", "2025-05-24T14:10:00"),
        Report("rep_20250523_004", "PROTECTED_ZONE", "PROCESSING", "2025-05-23T09:05:00"),
        Report("rep_20250522_005", "DOT_BLOCK", "COMPLETED", "2025-05-22T16:45:00"),
        Report("rep_20250521_006", "PROTECTED_ZONE", "REJECTED", "2025-05-21T13:30:00"),
        Report("rep_20250520_007", "DOT_BLOCK", "PROCESSING", "2025-05-20T10:00:00"),
        Report("rep_20250519_008", "PROTECTED_ZONE", "COMPLETED", "2025-05-19T17:15:00"),
        Report("rep_20250518_009", "DOT_BLOCK", "REJECTED", "2025-05-18T08:50:00"),
        Report("rep_20250517_010", "PROTECTED_ZONE", "PROCESSING", "2025-05-17T15:20:00"),
        Report("rep_20250516_011", "DOT_BLOCK", "COMPLETED", "2025-05-16T12:10:00"),
        Report("rep_20250515_012", "PROTECTED_ZONE", "REJECTED", "2025-05-15T09:00:00"),
        Report("rep_20250514_013", "DOT_BLOCK", "PROCESSING", "2025-05-14T11:40:00"),
        Report("rep_20250513_014", "PROTECTED_ZONE", "COMPLETED", "2025-05-13T13:55:00"),
        Report("rep_20250512_015", "DOT_BLOCK", "REJECTED", "2025-05-12T16:25:00"),
        Report("rep_20250511_016", "PROTECTED_ZONE", "PROCESSING", "2025-05-11T10:10:00"),
        Report("rep_20250510_017", "DOT_BLOCK", "COMPLETED", "2025-05-10T14:00:00"),
        Report("rep_20250509_018", "PROTECTED_ZONE", "REJECTED", "2025-05-09T08:30:00"),
        Report("rep_20250508_019", "DOT_BLOCK", "PROCESSING", "2025-05-08T17:45:00"),
        Report("rep_20250507_020", "PROTECTED_ZONE", "COMPLETED", "2025-05-07T09:20:00")
    )

    private lateinit var binding: ActivityReportListBinding
    private var currentPage = 1
    private var displayedList: List<Report> = testList
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

        binding.reportRecyclerView.layoutManager = LinearLayoutManager(this)
        applyFilterAndSort()

        binding.ivBack.setOnClickListener { finish() }
        binding.btnFilter.setOnClickListener { showCustomPopup(it) }
        binding.btnSortStatus.setOnClickListener { showSortPopup(it) }
    }

    // 정렬 버튼 누를 때 마다 페이지 새로고침
    private fun loadPage(page: Int) {
        currentPage = page
        val pageSize = 5
        val pageList = PaginationUtils.paginate(displayedList, page, pageSize)

        // 신고 내역 칸을 누르면 상세 신고로 넘어 가는 이벤트 리스너
        binding.reportRecyclerView.adapter = ReportAdapter(pageList) { report ->
            val intent = Intent(this, ReportDetailActivity::class.java)
            // 이건 API 연동하면 안해줘도 될 듯
            intent.putExtra("status", report.status)
            startActivity(intent)
        }

        val totalPages = PaginationUtils.getTotalPages(displayedList.size, pageSize)

        // 유틸 파일에 있는 페이지 네이션 함수 호출
        PaginationUiUtils.setupPagination(
            context = this,
            container = binding.paginationLayout,
            currentPage = page,
            totalPages = totalPages,
            onPageClick = { selectedPage -> loadPage(selectedPage) }
        )
    }

    // 상태와 정렬 기준에 동시에 맞게 페이지 로딩
    private fun applyFilterAndSort() {
        val filtered = testList.filter { currentFilter == null || it.status == currentFilter }
        displayedList = PaginationUtils.sortByDate(filtered, currentSortDescending)
        loadPage(1)
    }

    // 정렬 버튼 팝업 띄우기
    private fun showSortPopup(anchor: View) {
        val popupView = layoutInflater.inflate(R.layout.sort_popup_filter, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        popupView.findViewById<TextView>(R.id.item_latest).setOnClickListener {
            currentSortDescending = true
            binding.btnSortStatus.text = "최신순으로 나열"
            applyFilterAndSort()
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.item_oldest).setOnClickListener {
            currentSortDescending = false
            binding.btnSortStatus.text = "오래된 순으로 나열"
            applyFilterAndSort()
            popupWindow.dismiss()
        }

        popupWindow.elevation = 8f
        popupWindow.showAsDropDown(anchor, 0, 0)
    }

    // 상태 필터 버튼 팝업 띄우기
    private fun showCustomPopup(anchor: View) {
        val popupView = layoutInflater.inflate(R.layout.state_popup_filter, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        popupView.findViewById<TextView>(R.id.item_all).setOnClickListener {
            currentFilter = null
            binding.btnFilter.text = "전체 보기"
            applyFilterAndSort()
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.item_completed).setOnClickListener {
            currentFilter = "COMPLETED"
            binding.btnFilter.text = "승인만 보기"
            applyFilterAndSort()
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.item_processing).setOnClickListener {
            currentFilter = "PROCESSING"
            binding.btnFilter.text = "처리중만 보기"
            applyFilterAndSort()
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.item_rejected).setOnClickListener {
            currentFilter = "REJECTED"
            binding.btnFilter.text = "반려만 보기"
            applyFilterAndSort()
            popupWindow.dismiss()
        }

        popupWindow.elevation = 8f
        popupWindow.showAsDropDown(anchor, 0, 0)
    }
}
