package com.example.zikk

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zikk.databinding.ActivityNoticeBinding
import com.example.zikk.databinding.ActivityNoticeContextBinding

class NoticeContextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_notice_context)
        val binding = ActivityNoticeContextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            val intent = Intent(this, NoticeActivity::class.java)
            startActivity(intent)
        }
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, NoticeActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}