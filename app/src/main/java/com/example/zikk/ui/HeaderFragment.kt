package com.example.zikk.ui

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
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
        val menuButton = view.findViewById<ImageButton>(R.id.btn_menu)
        val searchEditText = view.findViewById<AutoCompleteTextView>(R.id.actv_search)
        val userButton = view.findViewById<ImageButton>(R.id.btn_user)

        val isLoggedIn = getTokenFromSharedPreferences()

        menuButton.setOnClickListener {
            // Activity에서 DrawerLayout 찾기
            val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.dl_container)
            drawerLayout.openDrawer(GravityCompat.START)
        }

        userButton.setOnClickListener {
            if(isLoggedIn == null) {
                showPhoneInputDialog()
            } else {
                Toast.makeText(
                    requireContext(),
                    "이미 로그인",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // 예시 자동완성 리스트
        val suggestions = listOf("사과", "바나나", "포도", "오렌지", "수박")

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
        searchEditText.setAdapter(adapter)

// 글자 입력 시 자동완성 리스트 보이게 함
        searchEditText.threshold = 1 // 1자 이상 입력하면 자동완성 보임

    }

    private fun showPhoneInputDialog() {
        // 이미 다이얼로그가 표시되어 있는지 확인
        val existingDialog = parentFragmentManager.findFragmentByTag("PhoneInputDialog")
        if (existingDialog != null) return

        val dialog = PhoneDialogFragment.newInstance { phoneNumber ->
            handlePhoneNumberInput(phoneNumber.toString())
        }

        dialog.show(parentFragmentManager, "PhoneInputDialog")
    }

    private fun handlePhoneNumberInput(phoneNumber: String) {
        // 전화번호 입력 완료 처리
        Toast.makeText(requireContext(), "연락처 등록: $phoneNumber", Toast.LENGTH_SHORT).show()
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }
}