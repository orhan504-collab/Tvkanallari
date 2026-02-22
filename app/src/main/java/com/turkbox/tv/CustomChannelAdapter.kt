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
    private val onClick: (Channel) -> Unit
) : RecyclerView.Adapter<CustomChannelAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: CardView = v.findViewById(R.id.cardView) // item_channel.xml'de bu ID olmalı
        val name: TextView = v.findViewById(R.id.tvChannelName)
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int): ViewHolder {
        val view = LayoutInflater.from(p.context).inflate(R.layout.item_channel, p, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(h: ViewHolder, p: Int) {
        val c = channels[p]
        h.name.text = c.name

        // TV KUMANDA ODAKLANMA (FOCUS) MANTIĞI
        h.itemView.isFocusable = true
        h.itemView.isFocusableInTouchMode = true

        h.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Kumanda ile üstüne gelindiğinde:
                h.card.setCardBackgroundColor(Color.parseColor("#E50914")) // Netflix Kırmızısı
                h.name.setTextColor(Color.WHITE)
                h.itemView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start() // Hafif büyüme
                
                // Sağdaki çerçevede oynatması için MainActivity'ye haber ver
                onFocus(c)
            } else {
                // Odak gidince eski haline dön:
                h.card.setCardBackgroundColor(Color.parseColor("#252525")) // Koyu Gri
                h.name.setTextColor(Color.LTGRAY)
                h.itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            }
        }

        // TIKLAMA MANTIĞI (Telefon ve Kumanda Tamam Tuşu)
        h.itemView.setOnClickListener {
            onClick(c)
        }
    }

    override fun getItemCount() = channels.size
}
