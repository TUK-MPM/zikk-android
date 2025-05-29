package com.example.zikk

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zikk.adapter.NoticeAdapter
import com.example.zikk.databinding.ActivityMainBinding
import com.example.zikk.databinding.ActivityReportListBinding
import com.example.zikk.databinding.ActivityReportWriteBinding

class ReportListActivity : BaseActivity() {
    private lateinit var binding: ActivityReportListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentViewWithBinding(ActivityReportListBinding::inflate)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}