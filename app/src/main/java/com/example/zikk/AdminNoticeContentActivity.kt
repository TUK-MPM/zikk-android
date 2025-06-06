package com.example.zikk

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zikk.databinding.ActivityAdminNoticeContextBinding

class AdminNoticeContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityAdminNoticeContextBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_admin_notice_context)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            val intent = Intent(this, AdminNoticeListActivity::class.java)
            startActivity(intent)
        }
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, AdminNoticeListActivity::class.java)
            startActivity(intent)
        }
        binding.btnDelete.setOnClickListener {
            val intent = Intent(this, AdminNoticeListActivity::class.java)
            startActivity(intent)
        } // 전환만 해놔서 내용 없애는 기능 넣어야 함
        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, AdminNoticeWriteActivity::class.java)
            startActivity(intent)
        } // 전환만 해놔서 내용 가지고 가는 기능 넣어야 함

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}