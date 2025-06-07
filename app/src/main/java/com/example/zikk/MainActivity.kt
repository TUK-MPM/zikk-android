package com.example.zikk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zikk.adapter.NoticeAdapter
import com.example.zikk.databinding.ActivityMainBinding
import com.example.zikk.model.Notice
import com.example.zikk.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var noticeAdapter: NoticeAdapter
    private val noticeList = mutableListOf<Notice>() // mutableList로 변경

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentViewWithBinding(ActivityMainBinding::inflate)

        noticeAdapter = NoticeAdapter(noticeList) { notice ->
            Toast.makeText(this@MainActivity, "${notice.title} 클릭됨", Toast.LENGTH_SHORT).show()
            Log.d("NoticeClick", "Clicked: ${notice.notiId}")
        }
        binding.rvNoticeList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = noticeAdapter
        }

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val displayCutoutInsets = insets.getInsets(WindowInsetsCompat.Type.displayCutout())

            // 안전한 패딩 계산
            val safePaddingTop = maxOf(systemBarsInsets.top, displayCutoutInsets.top)
            val safePaddingBottom = maxOf(systemBarsInsets.bottom, displayCutoutInsets.bottom)
            val safePaddingLeft = maxOf(systemBarsInsets.left, displayCutoutInsets.left)
            val safePaddingRight = maxOf(systemBarsInsets.right, displayCutoutInsets.right)
            v.setPadding(safePaddingLeft, safePaddingTop, safePaddingRight, safePaddingBottom)
            insets
        }

        // 공지 화면 불러오기
        binding.guideBtn.setOnClickListener {
            var intent = Intent(applicationContext, ReportGuide::class.java)
            startActivity(intent)
        }

        // 신고 작성 화면 불러오기
        binding.reportBtn.setOnClickListener {
            var intent = Intent(applicationContext, ReportWriteActivity::class.java)
            startActivity(intent)
        }

        // 신고 조회 화면 불러오기
        binding.reportQueryBtn.setOnClickListener {
            var intent = Intent(applicationContext, ReportListActivity::class.java)
            startActivity(intent)
        }

        // 공지사항 more 버튼 클릭 처리
        binding.tvNoticeMore.setOnClickListener {
            val intent = Intent(this, NoticeActivity::class.java) // 이동할 액티비티로 교체
            startActivity(intent)
        }

        getNotices()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getNotices() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getNotices(
                    size = 3,
                    page = 1
                )

                Log.d("response", response.toString())

                if (response.isSuccessful) {
                    val noticeListResponse = response.body()
                    noticeListResponse?.let {
                        // NoticeResponse에서 공지사항 리스트 꺼내기
                        val newList = it.content
                        noticeList.clear()
                        noticeList.addAll(newList)
                        noticeAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "응답 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "요청 실패", Toast.LENGTH_SHORT).show()
                Log.e("getNotices", "오류", e)
            }
        }
    }
}