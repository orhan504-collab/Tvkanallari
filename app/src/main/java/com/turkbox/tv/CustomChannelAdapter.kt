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

    // Çift tıklama kontrolü için zaman tutucu
    private var lastClickTime: Long = 0
    private val doubleClickTimeout = 300L

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        // En dıştaki view CardView olduğu için direkt v'yi CardView olarak alıyoruz
        val card: CardView = v as CardView
        val name: TextView = v.findViewById(R.id.tvChannelName)
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int): ViewHolder {
        val view = LayoutInflater.from(p.context).inflate(R.layout.item_channel, p, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(h: ViewHolder, p: Int) {
        val c = channels[p]
        h.name.text = c.name

        // KUMANDA ODAKLANMA (FOCUS) VE TELEFON TEK TIKLAMA MANTIĞI
        h.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Kumanda ile üstüne gelindiğinde veya telefonla tıklandığında:
                h.card.setCardBackgroundColor(Color.parseColor("#E50914")) // Kırmızı yap
                h.card.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start() // Büyüt
                h.card.cardElevation = 15f // Öne çıkar
                
                // SAĞDAKİ ÇERÇEVEDE OYNAT: MainActivity içindeki fonksiyonu tetikler
                onFocus(c)
            } else {
                // Odak gidince eski haline dön
                h.card.setCardBackgroundColor(Color.parseColor("#222222")) // Orijinal koyu renk
                h.card.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start() // Küçült
                h.card.cardElevation = 4f
            }
        }

        // TIKLAMA MANTIĞI (TAM EKRAN İÇİN)
        h.itemView.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            
            // Kumanda için tek tıklama yeterli, telefon için çift tıklama kontrolü
            if (currentTime - lastClickTime < doubleClickTimeout) {
                // ÇİFT TIKLAMA -> TAM EKRAN AÇ
                onClick(c)
            } else {
                // TEK TIKLAMADA ODAKLANMAYI SAĞLA (Böylece sağda açılır)
                h.itemView.requestFocus()
            }
            lastClickTime = currentTime
        }
    }

    override fun getItemCount() = channels.size
}
