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
        val cardView: CardView = v.findViewById(R.id.cardView) // layout dosmandaki ID ile eşleşmeli
        val name: TextView = v.findViewById(R.id.tvChannelName)
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int): ViewHolder {
        val view = LayoutInflater.from(p.context).inflate(R.layout.item_channel, p, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(h: ViewHolder, p: Int) {
        val c = channels[p]
        
        // 1. KANAL NUMARASINI VE İSMİNİ SET ET
        h.name.text = "${c.id}. ${c.name}"

        // 2. ODAKLANMA (FOCUS) EFEKTİ VE ZAPPING
        h.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Kumanda ile kanalın üzerine gelindiğinde
                h.cardView.setCardBackgroundColor(Color.parseColor("#FF6200EE")) // Mor tonu (aktif)
                h.name.setTextColor(Color.WHITE)
                h.cardView.cardElevation = 15f
                onFocus(c) // Kanal önizlemede otomatik oynasın
            } else {
                // Odak kanaldan çekildiğinde
                h.cardView.setCardBackgroundColor(Color.parseColor("#1E1E1E")) // Koyu gri (pasif)
                h.name.setTextColor(Color.LTGRAY)
                h.cardView.cardElevation = 4f
            }
        }

        // 3. TIKLAMA VE ÇİFT TIKLAMA MANTIĞI
        h.itemView.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < doubleClickTimeout) {
                onClick(c) // Çift tıklama: Tam ekran aç
            } else {
                onFocus(c) // Tek tıklama: Önizlemede oynat (Eğer focus değilse)
            }
            lastClickTime = currentTime
        }

        // 4. UZUN BASMA (DÜZENLE/SİL)
        h.itemView.setOnLongClickListener {
            onLongClick(c, p)
            true
        }
    }

    override fun getItemCount() = channels.size
}
