package com.example.zikk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.zikk.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        val binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnReoprt = findViewById<Button>(R.id.report)
        // 신고 작성 화면 넘어가기
        btnReoprt.setOnClickListener{
            var intent = Intent(applicationContext, ReportWriteActivity::class.java)
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
                    Toast.makeText(this@MainActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.localizedMessage?.let { Log.d("테스트 요청", it) }
                Toast.makeText(this@MainActivity, "요청 실패: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}