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
    private val onLongClick: (Channel, Int) -> Unit // Silme/Düzenleme için eklendi
) : RecyclerView.Adapter<CustomChannelAdapter.ViewHolder>() {

    private var lastClickTime: Long = 0
    private val doubleClickTimeout = 300L

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cardView: CardView = v as CardView
        val name: TextView = v.findViewById(R.id.tvChannelName)
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int): ViewHolder {
        val view = LayoutInflater.from(p.context).inflate(R.layout.item_channel, p, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(h: ViewHolder, p: Int) {
        val c = channels[p]
        h.name.text = c.name

        // KANAL GEÇİŞLERİNDE RENK DEĞİŞİMİ (ZAPPING)
        h.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Kumanda ile üzerine gelince veya seçilince renk: KIRMIZI
                h.cardView.setCardBackgroundColor(Color.parseColor("#E50914"))
                h.cardView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
                onFocus(c)
            } else {
                // Seçili değilken renk: KOYU GRİ
                h.cardView.setCardBackgroundColor(Color.parseColor("#222222"))
                h.cardView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            }
        }

        // TIKLAMA MANTIĞI
        h.itemView.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < doubleClickTimeout) {
                onClick(c)
            } else {
                h.itemView.requestFocus()
            }
            lastClickTime = currentTime
        }

        // UZUN BASINCA SİL/DÜZENLE (CONTEXT MENU)
        h.itemView.setOnLongClickListener {
            onLongClick(c, p)
            true
        }
    }

    override fun getItemCount() = channels.size
}
