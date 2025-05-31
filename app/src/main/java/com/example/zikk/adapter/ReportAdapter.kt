package com.example.zikk.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zikk.databinding.ItemReportBinding
import com.example.zikk.model.Report

class ReportAdapter(
    private val items: List<Report>,
    private val onItemClick: (Report) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(private val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(report: Report) {
            binding.txtTitle.text = convertWhere(report.where)
            binding.txtStatus.text = convertStatus(report.status)
            binding.txtDate.text = report.createdAt.replace("T", " ")

            // 🔹 상태별 배경색 적용 (LinearLayout 배경)
            val bgColor = when (report.status) {
                "PROCESSING" -> Color.parseColor("#FFF9C4") // 노랑
                "COMPLETED" -> Color.parseColor("#E3F2FD") // 파랑
                "REJECTED" -> Color.parseColor("#FCE4EC") // 분홍
                else -> Color.WHITE
            }
            binding.reportItemRoot.setBackgroundColor(bgColor)
            // 🔹 화살표 표시 조건
            binding.imageArrow.visibility = if (report.status == "REJECTED") View.GONE else View.VISIBLE
            binding.root.setOnClickListener{
                onItemClick(report)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    private fun convertWhere(where: String): String = when (where) {
        "DOT_BLOCK" -> "점자블록"
        "PROTECTED_ZONE" -> "보호구역"
        else -> "기타"
    }

    private fun convertStatus(status: String): String = when (status) {
        "PROCESSING" -> "검토중"
        "COMPLETED" -> "완료"
        "REJECTED" -> "반려"
        else -> "미정"
    }
}

