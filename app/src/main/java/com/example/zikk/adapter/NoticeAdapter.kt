package com.example.zikk.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zikk.R
import com.example.zikk.model.Notice

class NoticeAdapter(private var notices: List<Notice>, private val onItemClick: (Notice) -> Unit): RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> () {
    inner class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tv_notice_title)
        val dateTextView: TextView = itemView.findViewById(R.id.tv_notice_created_at)

        fun bind(notice: Notice) {
            titleTextView.text = notice.title
            dateTextView.text = notice.createdAt

            itemView.setOnClickListener {
                Log.d("NoticeAdapter", "${notice.title} 클릭됨")
                onItemClick(notice)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notice, parent, false)
        return NoticeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notices.size
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(notices[position])
    }
}