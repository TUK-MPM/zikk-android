package com.example.zikk

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zikk.databinding.ActivityReportDetailBinding
import com.example.zikk.databinding.ActivityReportListBinding
import coil.load

class ReportDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityReportDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentViewWithBinding(ActivityReportDetailBinding::inflate)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뒤로가기 버튼
        binding.ivBack.setOnClickListener {
            finish()
        }

        // ID 확인
        val reportId = intent.getStringExtra("reportId")


        // 🔹 임시 데이터
        val phoneNumber = "010-1234-5678"
        val address = "서울특별시 종로구 종로 1"
        val where = "DOT_BLOCK"
        val mediaUrls = listOf(
            "https://cdn.example.com/abc123.jpg",
            "https://cdn.example.com/def456.jpg"
        )


        // 1. 전화번호 세팅
        binding.etPhoneNum.setText(phoneNumber)

        // 2. 위치 세팅
        binding.etLocate.setText(address)

        // 3. where(enum) 값에 따라 라디오 버튼 선택
        when (where) {
            "DOT_BLOCK" -> binding.rbCrosswalk.isChecked = true
            "INTERSECTION" -> binding.rbIntersection.isChecked = true
            "PROTECTED_ZONE" -> binding.rbBusStop.isChecked = true
            "SIDEWALK" -> binding.rbSafetyZone.isChecked = true
            "ETC" -> binding.rbOthers.isChecked = true
        }

        // 4. 미디어 이미지 표시 (Glide 필요)
        binding.photoContainer.removeAllViews()
        mediaUrls.forEach { url ->
            val imageView = ImageView(this).apply {
                layoutParams = binding.btnPickImage.layoutParams
                scaleType = ImageView.ScaleType.CENTER_CROP
                setPadding(8, 8, 8, 8)
                load(url) // Coil 사용!
            }
            binding.photoContainer.addView(imageView)
        }

        // 5. 버튼 리스너
        binding.btnSubmit.setOnClickListener {
            // 수정 동작 구현 예정
        }

        binding.btnBack.setOnClickListener {
            finish()
        }


    }
}