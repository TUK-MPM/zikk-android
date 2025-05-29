package com.example.zikk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentViewWithBinding(ActivityMainBinding::inflate)

        val noticeList = listOf(
            Notice(1, "공지사항 1", "내용 1입니다.", "2020-01-01 12:00:00"),
            Notice(2, "공지사항 2", "내용 2입니다.", "2020-01-01 12:00:00"),
            Notice(3, "공지사항 3", "내용 3입니다.", "2020-01-01 12:00:00")
        )

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
        binding.guideBtn.setOnClickListener{
            var intent = Intent(applicationContext, ReportGuide::class.java)
            startActivity(intent)
        }

        // 신고 작성 화면 불러오기
        binding.reportBtn.setOnClickListener{
            var intent = Intent(applicationContext, ReportWriteActivity::class.java)
            startActivity(intent)
        }

        // 신고 조회 화면 불러오기
        binding.reportQueryBtn.setOnClickListener{
            var intent = Intent(applicationContext, ReportListActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getTodos() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getTodos()
                if (response.isSuccessful) {
                    val todos = response.body()
                    Log.d("테스트", todos?.get(0)?.title.toString())
                    Log.d("테스트", todos?.get(0)?.userId.toString())
                    Log.d("테스트", todos?.get(0)?.id.toString())
                    Log.d("테스트", todos?.get(0)?.completed.toString())
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "서버 오류: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.localizedMessage?.let { Log.d("테스트 요청", it) }
                Toast.makeText(
                    this@MainActivity,
                    "요청 실패: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}