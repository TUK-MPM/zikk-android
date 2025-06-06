package com.example.zikk

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zikk.databinding.ActivityAdminQuestionContextAcitivityBinding

class AdminQuestionContentAcitivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_admin_question_context_acitivity)
        val binding = ActivityAdminQuestionContextAcitivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            val intent = Intent(this, QuestionListActivity::class.java)
            startActivity(intent)
        }
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, QuestionListActivity::class.java)
            startActivity(intent)
        }
        binding.btnDelete.setOnClickListener {
            val intent = Intent(this, QuestionListActivity::class.java)
            startActivity(intent)
        } // 삭제되는 기능 넣어야 함
        binding.btnComplete.setOnClickListener {
            val intent = Intent(this, QuestionListActivity::class.java)
            startActivity(intent)
        } // 내용 옮기는 기능 넣어야 함
        


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}