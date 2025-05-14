package com.example.zikk

import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.zikk.databinding.ActivityMainBinding
import com.example.zikk.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.Response
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.test.setOnClickListener {
            getTodos()
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