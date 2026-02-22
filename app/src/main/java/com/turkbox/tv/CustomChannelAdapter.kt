package com.turkbox.tv

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CustomChannelAdapter(
    var channels: MutableList<Channel>,
    private val onFocus: (Channel) -> Unit,
    private val onClick: (Channel) -> Unit
) : RecyclerView.Adapter<CustomChannelAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: CardView = v.findViewById(R.id.cardView) // XML'deki CardView ID'niz
        val name: TextView = v.findViewById(R.id.tvChannelName)
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int): ViewHolder {
        val view = LayoutInflater.from(p.context).inflate(R.layout.item_channel, p, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(h: ViewHolder, p: Int) {
        val c = channels[p]
        h.name.text = c.name

        // Odaklanma (Focus) Efekti - TV Kumandası için
        h.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Seçiliyken büyü ve kırmızı ol
                view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
                h.card.setCardBackgroundColor(Color.parseColor("#E50914")) 
                h.name.setTextColor(Color.WHITE)
                onFocus(c)
            } else {
                // Seçili değilken eski haline dön
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                h.card.setCardBackgroundColor(Color.parseColor("#333333")) // Standart koyu gri
                h.name.setTextColor(Color.LTGRAY)
            }
        }

        // Tıklama (OK Tuşu) Mantığı
        h.itemView.setOnClickListener {
            onClick(c)
        }

        // Uzun Basma (Kanalı Silme) Mantığı
        h.itemView.setOnLongClickListener {
            AlertDialog.Builder(h.itemView.context, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                .setTitle("Kanalı Sil")
                .setMessage("${c.name} kanalını listeden kaldırmak istiyor musunuz?")
                .setPositiveButton("Evet") { _, _ ->
                    val pos = h.adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        channels.removeAt(pos)
                        notifyItemRemoved(pos)
                        // Not: MainActivity içinde saveChannels() çağrısı yapılması gerekir.
                    }
                }
                .setNegativeButton("Hayır", null)
                .show()
            true
        }
    }

    override fun getItemCount() = channels.size
}
