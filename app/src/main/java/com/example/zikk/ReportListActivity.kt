package com.example.zikk

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zikk.adapter.ReportAdapter
import com.example.zikk.databinding.ActivityReportListBinding
import com.example.zikk.model.Report
import com.example.zikk.network.RetrofitClient
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class ReportListActivity : BaseActivity() {
    // 임시 더비 데이터
    private  val testList = listOf(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentViewWithBinding(ActivityReportListBinding::inflate)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // RecyclerView 설정
        binding.reportRecyclerView.layoutManager = LinearLayoutManager(this)

        // 페이지 1부터 로딩 시작
        loadPage(1)
    }
// 추후 api 나오면 연동
/*    private fun loadPage(page: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getReports(page)
                if (response.isSuccessful) {
                    val body = response.body()
                    val reportList = body?.content ?: emptyList()
                    val totalPages = body?.totalPages ?: 1
                    currentPage = page

                    // 빈 목록 처리
                    if (reportList.isEmpty()) {
                        binding.reportRecyclerView.visibility = View.GONE
                        Toast.makeText(this@ReportListActivity, "신고가 없습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.reportRecyclerView.visibility = View.VISIBLE
                        binding.reportRecyclerView.adapter = ReportAdapter(reportList) { report ->
                            Toast.makeText(this@ReportListActivity, "Report ID: ${report.reportId}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    setupPagination(totalPages, currentPage)
                } else {
                    Toast.makeText(this@ReportListActivity, "불러오기 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReportListActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }*/


    //화면 불러오기
    private fun loadPage(page: Int) {
        val pageSize = 5
        val fromIndex = (page - 1) * pageSize
        val toIndex = minOf(fromIndex + pageSize, testList.size)
        val pageList = testList.subList(fromIndex, toIndex)

        binding.reportRecyclerView.adapter = ReportAdapter(pageList) {
            Toast.makeText(this, "클릭: ${it.reportId}", Toast.LENGTH_SHORT).show()
        }

        val totalPages = (testList.size + pageSize - 1) / pageSize
        setupPagination(totalPages, page)
    }

    // 페이지 네이션 함수
    private fun setupPagination(totalPages: Int, currentPage: Int) {
        val layout = binding.paginationLayout
        layout.removeAllViews()

        // ← 버튼
        val prev = createPageButton("<") {
            if (currentPage > 1) loadPage(currentPage - 1)
        }
        layout.addView(prev)

        // 페이지 번호
        val maxVisiblePages = 5
        val startPage = max(1, currentPage - 2)
        val endPage = min(totalPages, currentPage + 2)

        if (startPage > 1) {
            layout.addView(createPageButton("1") { loadPage(1) })
            if (startPage > 2) {
                layout.addView(createDots())
            }
        }

        for (i in startPage..endPage) {
            layout.addView(createPageButton(i.toString(), i == currentPage) {
                loadPage(i)
            })
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                layout.addView(createDots())
            }
            layout.addView(createPageButton(totalPages.toString()) { loadPage(totalPages) })
        }

        // → 버튼
        val next = createPageButton(">") {
            if (currentPage < totalPages) loadPage(currentPage + 1)
        }
        layout.addView(next)
    }

    private fun createPageButton(
        text: String,
        isCurrent: Boolean = false,
        onClick: () -> Unit
    ): TextView {
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

    private fun createDots(): TextView {
        return TextView(this).apply {
            text = "..."
            textSize = 16f
            setPadding(12, 6, 12, 6)
            setTextColor(Color.GRAY)
        }
    }

}
