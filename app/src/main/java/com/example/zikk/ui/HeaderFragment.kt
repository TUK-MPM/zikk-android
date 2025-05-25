package com.example.zikk.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.zikk.R

class HeaderFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_header, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuButton = view.findViewById<ImageButton>(R.id.menuBtn)
        val searchEditText = view.findViewById<AutoCompleteTextView>(R.id.search)

        menuButton.setOnClickListener {
            // Activity에서 DrawerLayout 찾기
            val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 예시 자동완성 리스트
        val suggestions = listOf("사과", "바나나", "포도", "오렌지", "수박")

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
        searchEditText.setAdapter(adapter)

// 글자 입력 시 자동완성 리스트 보이게 함
        searchEditText.threshold = 1 // 1자 이상 입력하면 자동완성 보임

    }

}