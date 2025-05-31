package com.example.zikk

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zikk.adapter.ReportAdapter
import com.example.zikk.databinding.ActivityReportListBinding
import com.example.zikk.model.Report
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.min

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
    // 현재 보고 있는 페이지 번호 (페이지네이션용)
    private var currentPage = 1
    // 필터 및 정렬이 적용된 실제 출력용 리스트
    private var displayedList: List<Report> = testList
    // 현재 필터링 상태 (null이면 전체 보기)
    private var currentFilter: String? = null
    // 현재 정렬 상태 (true면 최신순, false면 오래된순)
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
        loadPage(1)

        binding.ivBack.setOnClickListener { finish() }
        binding.btnFilter.setOnClickListener { showCustomPopup(it) }
        binding.btnSortStatus.setOnClickListener { showSortPopup(it) }
    }

    // 현재 페이지에 해당하는 리스트만 RecyclerView에 바인딩하고 페이지네이션 구성
    private fun loadPage(page: Int) {
        val pageSize = 5
        val fromIndex = (page - 1) * pageSize
        val toIndex = minOf(fromIndex + pageSize, displayedList.size)
        val pageList = displayedList.subList(fromIndex, toIndex)

        binding.reportRecyclerView.adapter = ReportAdapter(pageList) { report ->
            val intent = Intent(this, ReportDetailActivity::class.java)
            intent.putExtra("reportId", report.reportId)
            intent.putExtra("status", report.status)
            startActivity(intent)
        }

        val totalPages = (displayedList.size + pageSize - 1) / pageSize
        setupPagination(totalPages, page)
    }

    // 날짜 기준으로 오름차순 또는 내림차순 정렬한 후 1페이지부터 다시 표시
    private fun sortReportsByDate(descending: Boolean) {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        displayedList = if (descending) {
            displayedList.sortedByDescending { LocalDateTime.parse(it.createdAt, formatter) }
        } else {
            displayedList.sortedBy { LocalDateTime.parse(it.createdAt, formatter) }
        }
        loadPage(1)
    }

    // 현재 필터와 정렬 상태를 기준으로 리스트를 갱신한 후 1페이지부터 다시 표시
    private fun applyFilterAndSort() {
        displayedList = testList.filter { currentFilter == null || it.status == currentFilter }
            .let { filtered ->
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                if (currentSortDescending) {
                    filtered.sortedByDescending { LocalDateTime.parse(it.createdAt, formatter) }
                } else {
                    filtered.sortedBy { LocalDateTime.parse(it.createdAt, formatter) }
                }
            }
        loadPage(1)
    }
    // "전체 보기", "승인만 보기", "처리중만 보기", "반려만 보기" 팝업 구성 및 클릭 처리
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
    // "최신순", "오래된 순" 정렬 팝업 구성 및 클릭 처리
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
    // 페이지네이션 버튼 생성 및 이전/다음 페이지 이동 버튼 처리
    private fun setupPagination(totalPages: Int, currentPage: Int) {
        val layout = binding.paginationLayout
        layout.removeAllViews()

        layout.addView(createPageButton("<") {
            if (currentPage > 1) loadPage(currentPage - 1)
        })

        val startPage = max(1, currentPage - 2)
        val endPage = min(totalPages, currentPage + 2)

        if (startPage > 1) {
            layout.addView(createPageButton("1") { loadPage(1) })
            if (startPage > 2) layout.addView(createDots())
        }

        for (i in startPage..endPage) {
            layout.addView(createPageButton(i.toString(), i == currentPage) { loadPage(i) })
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) layout.addView(createDots())
            layout.addView(createPageButton(totalPages.toString()) { loadPage(totalPages) })
        }

        layout.addView(createPageButton(">") {
            if (currentPage < totalPages) loadPage(currentPage + 1)
        })
    }
    // 페이지 버튼(숫자 또는 '<' 또는 '>') 생성 함수
    private fun createPageButton(text: String, isCurrent: Boolean = false, onClick: () -> Unit): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 16f
            setPadding(12, 6, 12, 6)
            setOnClickListener { onClick() }
            setTextColor(if (isCurrent) Color.BLACK else Color.DKGRAY)
            paint.isUnderlineText = isCurrent
            setTypeface(null, if (isCurrent) Typeface.BOLD else Typeface.NORMAL)
        }
    }
    // 페이지네이션 중 생략(...) 표시용 텍스트뷰 생성
    private fun createDots(): TextView {
        return TextView(this).apply {
            text = "..."
            textSize = 16f
            setPadding(12, 6, 12, 6)
            setTextColor(Color.GRAY)
        }
    }
}
