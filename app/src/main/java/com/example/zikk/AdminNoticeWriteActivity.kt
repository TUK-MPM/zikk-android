package com.example.zikk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.zikk.databinding.ActivityAdminMainBinding
import com.example.zikk.databinding.ActivityAdminNoticeWriteBinding
import com.example.zikk.databinding.ActivityMainBinding
import com.example.zikk.enum.SortType
import com.example.zikk.extensions.getLoginToken
import com.example.zikk.model.request.CreateNoticeRequest
import com.example.zikk.network.RetrofitClient
import kotlinx.coroutines.launch

class AdminNoticeWriteActivity : BaseActivity() {
    private lateinit var binding: ActivityAdminNoticeWriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = setContentViewWithBinding(ActivityAdminNoticeWriteBinding::inflate)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.back.setOnClickListener {
            val intent = Intent(this, AdminNoticeListActivity::class.java)
            startActivity(intent)
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnComplete.setOnClickListener {
            createNotice()
        }
    }

    private fun createNotice() {
        lifecycleScope.launch {
            try {
                val token = "Bearer ${getLoginToken()!!}"
                Log.d("token", token)
                val request = CreateNoticeRequest(
                    binding.etTitle.text.toString(),
                    binding.etContent.text.toString()
                )
                val response = RetrofitClient.apiService.createNotice(
                    token,
                    request
                )

                Log.d("response", response.toString())

                if(response.isSuccessful) {
                    val noticeResponse = response.body()
                    noticeResponse?.let { responseBody ->
                        Toast.makeText(this@AdminNoticeWriteActivity, "완료", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }
}