package com.turkbox.tv

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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

        // Logoyu İnternetten Yükle
        Glide.with(h.itemView.context)
            .load(c.logo)
            .placeholder(android.R.drawable.ic_menu_report_image) // Yüklenirken çıkacak ikon
            .error(android.R.drawable.stat_notify_error) // Hata olursa çıkacak ikon
            .into(h.logo)

        h.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
                h.card.setCardBackgroundColor(Color.parseColor("#E50914")) // Seçilince Kırmızı
                onFocus(c)
            } else {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                h.card.setCardBackgroundColor(Color.parseColor("#222222")) // Normalde Koyu Gri
            }
        }

        h.itemView.setOnClickListener { onClick(c) }
    }

    override fun getItemCount() = channels.size
}
