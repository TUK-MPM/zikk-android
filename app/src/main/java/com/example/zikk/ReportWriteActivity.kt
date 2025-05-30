package com.example.zikk

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zikk.databinding.ActivityMainBinding
import com.example.zikk.databinding.ActivityReportListBinding
import com.example.zikk.databinding.ActivityReportWriteBinding

class ReportWriteActivity : BaseActivity() {
    private lateinit var binding: ActivityReportWriteBinding

    // 사진 선택 런처
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { addImageToLayout(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentViewWithBinding(ActivityReportWriteBinding::inflate)

        // 권한 요청
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        permissionLauncher.launch(permission)

        // 버튼 클릭 시 사진 선택 실행
        binding.btnPickImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }


    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "사진 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 선택한 이미지 추가
    private fun addImageToLayout(uri: Uri) {
        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(61.dp, 65.dp).apply {
                marginEnd = 10.dp
            }
            setImageURI(uri)
            scaleType = ImageView.ScaleType.CENTER_CROP

            setOnClickListener {
                AlertDialog.Builder(this@ReportWriteActivity)
                    .setTitle("사진 삭제")
                    .setMessage("이 사진을 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { _, _ ->
                        binding.photoContainer.removeView(this)
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        }

        // 버튼 바로 앞에 이미지 추가
        val index = binding.photoContainer.indexOfChild(binding.btnPickImage)
        binding.photoContainer.addView(imageView, index)
    }

    // dp 변환 확장 함수
    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}