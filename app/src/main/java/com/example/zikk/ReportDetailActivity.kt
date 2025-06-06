package com.example.zikk

import android.Manifest
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.zikk.databinding.ActivityReportDetailBinding
import com.example.zikk.enum.IllegalParkingLocation
import com.example.zikk.extensions.getLoginToken
import com.example.zikk.model.ReportDetail
import com.example.zikk.model.request.ReportRequest
import com.example.zikk.network.RetrofitClient
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ReportDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityReportDetailBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var reportId: String? = null
    private lateinit var token: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentViewWithBinding(ActivityReportDetailBinding::inflate)
        enableEdgeToEdge()

        // 토큰 유효성 체크
        val rawToken = getLoginToken()
        if (rawToken == null) {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        token = "Bearer $rawToken"

        // 시스템 바 패딩 적용
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 전달받은 reportId 추출
        reportId = intent.getStringExtra("reportId")

        // 뒤로가기 버튼 동작
        binding.ivBack.setOnClickListener { finish() }
        // 뒤로가기 버튼 동작
        binding.btnBack.setOnClickListener { finish() }

        // 위치 다시 가져오기
        binding.btnLocateGet.setOnClickListener { getCurrentLocation() }
        // 신고 수정 버튼
        binding.btnSubmit.setOnClickListener { updateReport() }

        // 서버에서 상세 정보 조회
        reportId?.let { fetchReportDetail(it) }
    }

    // 상세 데이터 서버에서 조회
    private fun fetchReportDetail(reportId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getReportDetail(token, reportId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val reportDetail = response.body()!! // 타입: ReportDetail
                        populateFields(reportDetail)
                    } else {
                        Toast.makeText(this@ReportDetailActivity, "조회 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ReportDetailActivity, "에러: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 조회한 데이터를 화면에 표시
    private fun populateFields(report: ReportDetail) {
        // 전화번호, 주소 표시
        binding.etPhoneNum.setText(report.number)
        binding.etLocateWrite.setText(report.address)

        // 신고 유형(RadioButton) 선택
        try {
            when (IllegalParkingLocation.valueOf(report.where)) {
                IllegalParkingLocation.DOT_BLOCK -> binding.rbCrosswalk.isChecked = true
                IllegalParkingLocation.TRAFFIC_ISLAND -> binding.rbIntersection.isChecked = true
                IllegalParkingLocation.PROTECTED_ZONE -> binding.rbBusStop.isChecked = true
                IllegalParkingLocation.WALKWAY_OTHER -> binding.rbSafetyZone.isChecked = true
                IllegalParkingLocation.OTHER -> binding.rbOthers.isChecked = true
            }
        } catch (e: Exception) {
            binding.rbOthers.isChecked = true
        }

        // 상태 표시 (텍스트뷰 추가 필요, 예: binding.tvStatus)
        val statusText = when (report.status) {
            "COMPLETED" -> "완료"
            "REJECTED" -> "반려"
            else -> "처리중"
        }
        // 만약 상태 텍스트뷰가 있다면 아래 주석 해제
        // binding.tvStatus.text = statusText

        // 첨부 사진 리스트를 LinearLayout에 추가 (기존 뷰는 삭제)
        binding.photoContainer.removeAllViews()
        report.mediaUrls.forEach { url ->
            val imageView = ImageView(this).apply {
                layoutParams = binding.btnPickImage.layoutParams
                scaleType = ImageView.ScaleType.CENTER_CROP
                setPadding(8, 8, 8, 8)
                load(url) // coil로 이미지 로딩
            }
            binding.photoContainer.addView(imageView)
        }

        // 이미 완료/반려된 신고는 수정불가 처리
        if (report.status == "COMPLETED" || report.status == "REJECTED") {
            binding.btnSubmit.apply {
                isEnabled = false
                setBackgroundColor(ContextCompat.getColor(context, R.color.deap_gray))
            }
        }
    }

    // 신고 내용 수정 (신고자, 주소, 유형만 수정 가능 예시)
    private fun updateReport() {
        val phone = binding.etPhoneNum.text.toString().trim()
        val address = binding.etLocateWrite.text.toString().trim()
        val selectedType = when (binding.radioGroup.checkedRadioButtonId) {
            R.id.rb_crosswalk -> IllegalParkingLocation.DOT_BLOCK.name
            R.id.rb_intersection -> IllegalParkingLocation.TRAFFIC_ISLAND.name
            R.id.rb_bus_stop -> IllegalParkingLocation.PROTECTED_ZONE.name
            R.id.rb_safety_zone -> IllegalParkingLocation.WALKWAY_OTHER.name
            R.id.rb_others -> IllegalParkingLocation.OTHER.name
            else -> IllegalParkingLocation.OTHER.name
        }

        if (phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageUrls = listOf<String>() // TODO: presigned URL 업로드 구현 필요(필요 시)
                val request = ReportRequest(phone, address, selectedType, imageUrls)
                val response = RetrofitClient.apiService.updateReport(reportId!!, request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ReportDetailActivity, response.body()?.message ?: "수정 완료", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ReportDetailActivity, "수정 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ReportDetailActivity, "에러: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 현재 위치(주소) 받아오기
    private fun getCurrentLocation() {
        val fineGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        val coarseGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            requestPermissions(permissions, 1001)
            return
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return
                val address = try {
                    val geocoder = Geocoder(this@ReportDetailActivity, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    addresses?.getOrNull(0)?.getAddressLine(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                } ?: "주소를 변환할 수 없습니다."
                binding.etLocateWrite.setText(address)
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMaxUpdates(1)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // 바깥 클릭 시 키보드 숨김 처리
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}
