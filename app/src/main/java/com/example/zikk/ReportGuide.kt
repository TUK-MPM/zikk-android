package com.example.zikk

import android.content.Intent // 추가
import android.os.Bundle
import android.view.View       // 추가
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zikk.databinding.ActivityReportGuideBinding

class ReportGuide : BaseActivity() {
    private lateinit var binding: ActivityReportGuideBinding
    private var isNavigated = false // 중복 실행 방지용 플래그

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentViewWithBinding(ActivityReportGuideBinding::inflate)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets // 반드시 반환
        }

        binding.btnClose.setOnClickListener {
            finish()
        }

        // 스크롤이 맨 아래까지 도달하면 다음 화면으로 이동
        binding.main.setOnScrollChangeListener { v: View, _, scrollY, _, _ ->
            val scrollView = v as ScrollView
            val child = scrollView.getChildAt(0)

            val triggerPosition = (child.measuredHeight - scrollView.measuredHeight) * 0.9

            if (!isNavigated && scrollY >= triggerPosition) {
                isNavigated = true

                // 애니메이션 → 전환
                binding.main.animate()
                    .translationY(-100f)
                    .setDuration(300)
                    .withEndAction {
                        val intent = Intent(this, ReportWriteActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_up, R.anim.none)
                        finish() // 화면 종료
                    }
            }
        }
    }
}
