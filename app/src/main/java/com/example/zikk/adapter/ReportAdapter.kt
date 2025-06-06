package com.example.zikk.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zikk.databinding.ItemReportBinding
import com.example.zikk.enum.IllegalParkingLocation
import com.example.zikk.enum.Status
import com.example.zikk.model.Report

class ReportAdapter(
    private val items: List<Report>,
    private val onItemClick: (Report) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    // 상태별 배경색 상수 맵
    private val statusBgColors = mapOf(
        "PROCESSING" to Color.parseColor("#FFF9C4"), // 노랑
        "COMPLETED" to Color.parseColor("#E3F2FD"), // 파랑
        "REJECTED" to Color.parseColor("#FCE4EC")    // 분홍
    )

    inner class ReportViewHolder(private val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(report: Report) {
            // 위치 enum 변환
            val whereDesc = try {
                IllegalParkingLocation.valueOf(report.where).description
            } catch (e: Exception) {
                "기타"
            }
            // 상태 enum 변환
            val statusDesc = try {
                Status.valueOf(report.status).description
            } catch (e: Exception) {
                "미정"
            }

            binding.txtTitle.text = whereDesc
            binding.txtStatus.text = statusDesc
            binding.txtDate.text = report.createdAt.replace("T", " ")

            val bgColor = statusBgColors[report.status] ?: Color.WHITE
            binding.reportItemRoot.setBackgroundColor(bgColor)
            binding.imageArrow.visibility = if (report.status == "REJECTED") View.GONE else View.VISIBLE

            binding.root.setOnClickListener { onItemClick(report) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        android.util.Log.d("ReportAdapter", "bind position=$position: ${items[position]}")
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}