package com.example.zikk.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.zikk.R

class PhoneDialogFragment : DialogFragment() {

    private var onPhoneEnteredListener: ((String) -> Unit)? = null
    private lateinit var etPhone: EditText
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        return dialog
    }

    private fun initViews(view: View) {
        etPhone = view.findViewById(R.id.et_phone)
        btnConfirm = view.findViewById(R.id.btn_confirm)

        // 초기 상태에서 확인 버튼 비활성화
        btnConfirm.isEnabled = false
    }


    companion object {
        @JvmStatic
        fun newInstance(param: (Any) -> Unit) =
            PhoneDialogFragment().apply {

            }
    }
}