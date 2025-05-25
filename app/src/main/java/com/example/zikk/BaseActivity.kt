package com.example.zikk

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout

abstract class BaseActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DrawerDebug", "BaseActivity onCreate called")
    }

    override fun setContentView(layoutResID: Int) {
        // 1. base 레이아웃 inflate
        val fullLayout = layoutInflater.inflate(R.layout.activity_base, null)

        // 2. baseContent 영역에 실제 액티비티 레이아웃 inflate
        val container = fullLayout.findViewById<FrameLayout>(R.id.baseContent)
        layoutInflater.inflate(layoutResID, container, true)

        // 3. 화면에 최종 세팅
        super.setContentView(fullLayout)

        // 4. 드로어 초기화
        drawerLayout = findViewById(R.id.drawerLayout)
        setupDrawer()
    }

    private fun setupDrawer() {
        val drawerContainer = findViewById<FrameLayout>(R.id.drawerContainer)
        layoutInflater.inflate(R.layout.layout_drawer, drawerContainer, true)
    }
}
