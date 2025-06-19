package com.example.zikk.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.example.zikk.R

object PopupUtils {

    fun showSortPopup(
        context: Context,
        anchor: View,
        onSortSelected: (descending: Boolean) -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.sort_popup_filter, null)
        val popup = PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        view.findViewById<TextView>(R.id.item_latest).setOnClickListener {
            onSortSelected(true)
            popup.dismiss()
        }

        view.findViewById<TextView>(R.id.item_oldest).setOnClickListener {
            onSortSelected(false)
            popup.dismiss()
        }

        popup.elevation = 8f
        popup.showAsDropDown(anchor)
    }
}