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

    private var lastClickTime: Long = 0
    private val doubleClickTimeout = 300L

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        // XML'de id aramıyoruz, root view'un CardView olduğunu biliyoruz
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

        // Kumanda Odağı (Zapping Mantığı)
        h.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Odaklanınca kırmızı yap ve büyüt
                h.cardView.setCardBackgroundColor(Color.parseColor("#E50914"))
                h.cardView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
                // Sağdaki çerçevede oynat
                onFocus(c)
            } else {
                // Odak gidince orijinal renge dön
                h.cardView.setCardBackgroundColor(Color.parseColor("#222222"))
                h.cardView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            }
        }

        // Tıklama Mantığı
        h.itemView.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < doubleClickTimeout) {
                // Çift Tıklama -> Tam Ekran
                onClick(c)
            } else {
                // Tek Tıklama -> Odağı buraya çek (Sağda açılmasını sağlar)
                h.itemView.requestFocus()
            }
            lastClickTime = currentTime
        }
    }

    override fun getItemCount() = channels.size
}
