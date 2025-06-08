package com.example.zikk

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zikk.databinding.ActivityAdminNoticeListBinding
import com.example.zikk.model.Notice
import com.example.zikk.network.RetrofitClient
import com.example.zikk.util.PaginationUtils
import kotlinx.coroutines.launch

class AdminNoticeListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityAdminNoticeListBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_admin_notice_list)
        setContentView(binding.root)

        val spinner: Spinner = binding.mySpinner
        val items = arrayOf("최신순", "오래된순")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = adapter

        binding.back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnWrite.setOnClickListener {
            val intent = Intent(this, AdminNoticeWriteActivity::class.java)
            startActivity(intent)
        }
        binding.btnNoticeContext.setOnClickListener {
            val intent = Intent(this, AdminNoticeContentActivity::class.java)
            startActivity(intent)
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}