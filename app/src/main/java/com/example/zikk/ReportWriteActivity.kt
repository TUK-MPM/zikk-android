package com.example.zikk

import android.Manifest
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AlertDialog
import com.example.zikk.databinding.ActivityReportWriteBinding
import com.google.android.gms.location.*
import java.util.Locale

class ReportWriteActivity : BaseActivity() {

    private lateinit var binding: ActivityReportWriteBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    // 사진 선택 런처
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { addImageToLayout(it) }
    }

    // 사진 권한 요청 런처
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "사진 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentViewWithBinding(ActivityReportWriteBinding::inflate)

        enableEdgeToEdge()

        // 시스템 바 여백 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 위치 서비스 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 사진 권한 요청
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        permissionLauncher.launch(permission)

        // 뒤로가기 버튼
        binding.ivBack.setOnClickListener {
            finish()
        }

        // 사진 추가 버튼
        binding.btnPickImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // 위치 가져오기 버튼
        binding.btnLocateGet.setOnClickListener {
            getCurrentLocation()
        }
    }

    // 위치 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 위치 요청 및 주소 변환
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // 단 한 번만 위치 요청
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
            numUpdates = 1
        }

        // 위치 결과 콜백 정의
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude

                    val coordText = "위도: $lat, 경도: $lon"

                    val address = try {
                        val geocoder = Geocoder(this@ReportWriteActivity, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(lat, lon, 1)
                        if (!addresses.isNullOrEmpty()) {
                            addresses[0].getAddressLine(0)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }

                    val resultText = address ?: "주소를 변환할 수 없습니다."
                    binding.etLocateWrite.setText(resultText)

                    // 콜백 해제
                    fusedLocationClient.removeLocationUpdates(this)
                } else {
                    Toast.makeText(this@ReportWriteActivity, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 위치 요청 시작
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
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

        val index = binding.photoContainer.indexOfChild(binding.btnPickImage)
        binding.photoContainer.addView(imageView, index)
    }

    // dp 단위 변환 확장 함수
    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()

    // 화면 다른 화면 터치시 키보드 사라짐
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

}
