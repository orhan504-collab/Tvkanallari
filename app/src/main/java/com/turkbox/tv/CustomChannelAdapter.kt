package com.turkbox.tv

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CustomChannelAdapter(
    private val channels: List<Channel>,
    private val onFocus: (Channel) -> Unit,
    private val onClick: (Channel) -> Unit
) : RecyclerView.Adapter<CustomChannelAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: CardView = v as CardView
        val name: TextView = v.findViewById(R.id.tvChannelName)
        val logo: ImageView = v.findViewById(R.id.imgChannelLogo)
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int): ViewHolder {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_channel, p, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(h: ViewHolder, p: Int) {
        val c = channels[p]
        h.name.text = c.name

        // Yüklediğin resmi buradan bağladık
        // ÖNEMLİ: Resim ismin farklıysa (örneğin logo.png ise) burayı R.drawable.logo yap
        h.logo.setImageResource(R.drawable.kanal_logo)

        h.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Seçilen kanal büyüyecek ve kırmızı olacak
                view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).start()
                h.card.setCardBackgroundColor(Color.parseColor("#E50914"))
                h.card.cardElevation = 15f
                onFocus(c)
            } else {
                // Odak gidince eski gri haline dönecek
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
                h.card.setCardBackgroundColor(Color.parseColor("#222222"))
                h.card.cardElevation = 4f
            }
        }

        h.itemView.setOnClickListener { onClick(c) }
    }

    override fun getItemCount() = channels.size
}
