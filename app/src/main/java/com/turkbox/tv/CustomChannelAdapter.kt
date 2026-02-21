package com.turkbox.tv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomChannelAdapter(
    private val channels: List<Channel>,
    private val onFocus: (Channel) -> Unit,
    private val onClick: (Channel) -> Unit
) : RecyclerView.Adapter<CustomChannelAdapter.ChannelViewHolder>() {

    class ChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        // Standart Android liste görünümünü kullanıyoruz (basitlik için)
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channels[position]
        holder.tvName.text = channel.name
        holder.tvName.setTextColor(android.graphics.Color.WHITE)

        // Kumanda ile üzerine gelindiğinde (Focus)
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onFocus(channel)
                holder.itemView.setBackgroundColor(android.graphics.Color.RED) // Seçili kanal rengi
            } else {
                holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
        }

        // Tıklandığında
        holder.itemView.setOnClickListener {
            onClick(channel)
        }
    }

    override fun getItemCount() = channels.size
}
