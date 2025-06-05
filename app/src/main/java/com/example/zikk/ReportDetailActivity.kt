package com.example.zikk

import android.Manifest
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.zikk.databinding.ActivityReportDetailBinding
import com.example.zikk.enum.IllegalParkingLocation
import com.example.zikk.model.Report
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

    private val imageUriList = mutableListOf<Uri>()
    private var reportId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentViewWithBinding(ActivityReportDetailBinding::inflate)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        reportId = intent.getStringExtra("reportId")
        val status = intent.getStringExtra("status") ?: "PROCESSING"

        val statusKor = when (status) {
            "COMPLETED" -> "완료"
            "REJECTED" -> "반려"
            else -> "처리중"
        }

        if (statusKor == "완료" || statusKor == "반려") {
            binding.btnSubmit.apply {
                isEnabled = false
                setBackgroundColor(ContextCompat.getColor(context, R.color.deap_gray))
            }
        }

        reportId?.let { fetchReportDetail(it) }

        binding.ivBack.setOnClickListener { finish() }
        binding.btnLocateGet.setOnClickListener { getCurrentLocation() }
        binding.btnSubmit.setOnClickListener { updateReport() }
    }

    private fun fetchReportDetail(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getReportDetail(id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val report = response.body()!!
                        populateFields(report)
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

    private fun populateFields(report: Report) {
        binding.etPhoneNum.setText(report.reporterContact ?: "")
        binding.etLocateWrite.setText(report.address ?: "")

        when (IllegalParkingLocation.valueOf(report.where)) {
            IllegalParkingLocation.DOT_BLOCK -> binding.rbCrosswalk.isChecked = true
            IllegalParkingLocation.TRAFFIC_ISLAND -> binding.rbIntersection.isChecked = true
            IllegalParkingLocation.PROTECTED_ZONE -> binding.rbBusStop.isChecked = true
            IllegalParkingLocation.WALKWAY_OTHER -> binding.rbSafetyZone.isChecked = true
            IllegalParkingLocation.OTHER -> binding.rbOthers.isChecked = true
        }

        binding.photoContainer.removeAllViews()
        report.mediaUrls?.forEach { url ->
            val imageView = ImageView(this).apply {
                layoutParams = binding.btnPickImage.layoutParams
                scaleType = ImageView.ScaleType.CENTER_CROP
                setPadding(8, 8, 8, 8)
                load(url)
            }
            binding.photoContainer.addView(imageView)
        }
    }

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
                val imageUrls = listOf<String>() // TODO: presigned URL 업로드 구현 필요
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

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}
