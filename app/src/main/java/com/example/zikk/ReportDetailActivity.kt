package com.example.zikk

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import coil.load
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.zikk.databinding.ActivityReportDetailBinding
import com.example.zikk.enum.IllegalParkingLocation
import com.example.zikk.extensions.getLoginToken
import com.example.zikk.extensions.getUserRole
import com.example.zikk.model.ReportDetail
import com.example.zikk.model.request.ReportRequest
import com.example.zikk.model.request.ReportStatusRequest
import com.example.zikk.model.request.PatchReportRequest
import com.example.zikk.network.RetrofitClient
import com.google.android.gms.location.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Locale

class ReportDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityReportDetailBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var reportId: String? = null
    private lateinit var token: String

    private val newImageUris = mutableListOf<Uri>()
    private val existingImageUrls = mutableListOf<String>()
    private val deletedImageUrls = mutableListOf<String>()

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            newImageUris.add(it)
            addImageToLayout(uri, isExisting = false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentViewWithBinding(ActivityReportDetailBinding::inflate)
        binding.llAdminBtnGroup.visibility = if (isAdmin()) View.VISIBLE else View.GONE
        binding.llUserBtnGroup.visibility = if (isAdmin()) View.GONE else View.VISIBLE

        enableEdgeToEdge()

        val rawToken = getLoginToken()
        if (rawToken == null) {
            Toast.makeText(this, "\uB85C\uADF8\uC778 \uC815\uBCF4\uAC00 \uC5C6\uC2B5\uB2C8\uB2E4.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        token = "Bearer $rawToken"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        reportId = intent.getStringExtra("reportId")

        binding.ivBack.setOnClickListener { finish() }
        binding.btnBack.setOnClickListener { finish() }
        binding.btnLocateGet.setOnClickListener { getCurrentLocation() }
        binding.btnSubmit.setOnClickListener {
            reportId?.toLongOrNull()?.let { id -> updateReport(id) }
        }
        binding.btnPickImage.setOnClickListener { imagePickerLauncher.launch("image/*") }

        reportId?.let { fetchReportDetail(it) }

        if(isAdmin()) {
            binding.btnConfirm.setOnClickListener {
                reportId?.toInt()?.let { it1 -> processReportStatus("COMPLETED", it1) }
            }

            binding.btnReject.setOnClickListener {
                reportId?.toInt()?.let { it1 -> processReportStatus("REJECTED", it1) }
            }
        }
    }

    private fun fetchReportDetail(reportId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getReportDetail(token, reportId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val reportDetail = response.body()
                        if (reportDetail != null) {
                            populateFields(reportDetail)
                        } else {
                            Toast.makeText(this@ReportDetailActivity, "조회 결과가 비어 있습니다.", Toast.LENGTH_SHORT).show()
                            Log.e("fetchReportDetail", "response body is null")
                        }
                    } else {
                        Toast.makeText(this@ReportDetailActivity, "조회 실패", Toast.LENGTH_SHORT)
                            .show()
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@ReportDetailActivity, "조회 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                        Log.e("fetchReportDetail", "조회 실패: code=${response.code()}, error=$errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ReportDetailActivity,
                        "에러: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Toast.makeText(this@ReportDetailActivity, "에러: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("fetchReportDetail", "예외 발생", e)
                }
            }
        }
    }

    private fun populateFields(report: ReportDetail) {
        binding.etPhoneNum.setText(report.number)
        binding.etLocateWrite.setText(report.address)

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

        binding.photoContainer.removeAllViews()
        existingImageUrls.clear()
        existingImageUrls.addAll(report.mediaUrls)

        report.mediaUrls.forEach { url ->
            addImageToLayout(Uri.parse(url), isExisting = true)
        }

        binding.photoContainer.removeView(binding.btnPickImage)
        binding.photoContainer.addView(binding.btnPickImage)

        if (report.status == "COMPLETED" || report.status == "REJECTED") {
            binding.btnSubmit.apply {
                isEnabled = false
                setBackgroundColor(ContextCompat.getColor(context, R.color.deap_gray))
            }
        }
    }

    private fun addImageToLayout(uri: Uri, isExisting: Boolean) {
        val imageView = ImageView(this).apply {
            layoutParams = binding.btnPickImage.layoutParams
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(8, 8, 8, 8)

            if (isExisting) {
                load(uri.toString())
            } else {
                setImageURI(uri)
            }

            setOnClickListener {
                AlertDialog.Builder(this@ReportDetailActivity)
                    .setTitle("사진 삭제")
                    .setMessage("이 사진을 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { _, _ ->
                        if (isExisting) {
                            deletedImageUrls.add(uri.toString())
                            existingImageUrls.remove(uri.toString())
                        } else {
                            newImageUris.remove(uri)
                        }
                        binding.photoContainer.removeView(this)
                        binding.photoContainer.removeView(binding.btnPickImage)
                        binding.photoContainer.addView(binding.btnPickImage)
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        }

        val index = binding.photoContainer.indexOfChild(binding.btnPickImage)
        binding.photoContainer.addView(imageView, index)
    }

    private fun updateReport(reportId: Long) {
        val phone = binding.etPhoneNum.text.toString().trim()
        val address = binding.etLocateWrite.text.toString().trim()
        val status = "PENDING"
        val patchRequest = PatchReportRequest(phone, address, status, existingImageUrls)
        val requestJson = Gson().toJson(patchRequest)
        val requestBody = requestJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val imageParts = createImageMultipartList(this, newImageUris)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.updateReportWithImages(
                    reportId,
                    requestBody,
                    if (imageParts.isNotEmpty()) imageParts else null
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ReportDetailActivity,
                            response.body()?.message ?: "수정 완료",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@ReportDetailActivity,
                            "수정 실패: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Toast.makeText(this@ReportDetailActivity, "수정 완료", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Log.e("updateReport", "수정 실패 code=${response.code()}, error=${response.errorBody()?.string()}")
                        Toast.makeText(this@ReportDetailActivity, "수정 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("updateReport", "예외 발생", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ReportDetailActivity,
                        "에러: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun createImageMultipartList(context: Context, uris: List<Uri>): List<MultipartBody.Part> {
        return uris.mapIndexed { index, uri ->
            val inputStream = context.contentResolver.openInputStream(uri)!!
            val bytes = inputStream.readBytes()
            val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("images", "image$index.jpg", requestFile)
        }
    }

    private fun getCurrentLocation() {
        val fineGranted =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        val coarseGranted =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        val fineGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val coarseGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED

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
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isAdmin(): Boolean {
        if (getUserRole() == "ROLE_ADMIN") {
            return true
        } else {
            return false
        }
    }

    private fun processReportStatus(status: String, reportId: Int) {
        lifecycleScope.launch {
            try {
                val token = "Bearer " + getLoginToken()
                val request = ReportStatusRequest(status)
                val response =
                    RetrofitClient.apiService.processReportStatus(token, reportId, request)

                Log.d("REQUEAST", request.toString())
                Log.d("RESPONSE", response.toString())
                if(response.isSuccessful) {
                    Toast.makeText(this@ReportDetailActivity, response.body()?.message ?: "", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ReportDetailActivity, "요청 실패", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@ReportDetailActivity, "요청 실패", Toast.LENGTH_SHORT).show()
                throw e
            }
        }
    }
}
