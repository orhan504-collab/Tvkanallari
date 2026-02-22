package com.turkbox.tv

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CustomChannelAdapter(
    var channels: MutableList<Channel>,
    private val onFocus: (Channel) -> Unit,
    private val onClick: (Channel) -> Unit,
    private val onLongClick: (Channel, Int) -> Unit
) : RecyclerView.Adapter<CustomChannelAdapter.ViewHolder>() {

    private var lastClickTime: Long = 0
    private val doubleClickTimeout = 300L

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        // Eğer layout dosyasındaki ID farklıysa bile hata vermemesi için 'as CardView' kullandık
        val card: CardView = v as CardView
        val name: TextView = v.findViewById(R.id.tvChannelName)
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int): ViewHolder {
        val view = LayoutInflater.from(p.context).inflate(R.layout.item_channel, p, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(h: ViewHolder, p: Int) {
        val c = channels[p]
        
        // Kanal numarasını ve ismini göster (e.g., 1. TRT 1)
        h.name.text = "${c.id}. ${c.name}"

        // ODAKLANMA (TV KUMANDASI İÇİN)
        h.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                h.card.setCardBackgroundColor(Color.parseColor("#444444")) // Seçili renk
                h.name.setTextColor(Color.WHITE)
                onFocus(c) // Kanalı önizlemede oynat
            } else {
                h.card.setCardBackgroundColor(Color.parseColor("#1A1A1A")) // Normal renk
                h.name.setTextColor(Color.LTGRAY)
            }
        }

        // TIKLAMA (FULL SCREEN)
        h.itemView.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < doubleClickTimeout) {
                onClick(c)
            }
            lastClickTime = currentTime
        }

        // UZUN BASMA (SİLME/DÜZENLEME)
        h.itemView.setOnLongClickListener {
            onLongClick(c, p)
            true
        }
    }

    override fun getItemCount() = channels.size
}
