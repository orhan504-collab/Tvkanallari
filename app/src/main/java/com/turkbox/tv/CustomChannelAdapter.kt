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
        // XML'de id olmadığı için 'v as CardView' diyerek en dıştaki CardView'u alıyoruz
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

        // Odaklanma (Focus) Efekti - TV Kumandası için
        h.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Seçiliyken büyü ve kırmızı ol
                view.animate().scaleX(1.08f).scaleY(1.08f).setDuration(200).start()
                h.card.setCardBackgroundColor(Color.parseColor("#E50914")) 
                h.name.setTextColor(Color.WHITE)
                onFocus(c)
            } else {
                // Seçili değilken eski haline (senin belirlediğin #222222 rengine) dön
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                h.card.setCardBackgroundColor(Color.parseColor("#222222")) 
                h.name.setTextColor(Color.parseColor("#BBBBBB"))
            }
        }

        h.itemView.setOnClickListener {
            onClick(c)
        }

        h.itemView.setOnLongClickListener {
            AlertDialog.Builder(h.itemView.context, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                .setTitle("Kanalı Sil")
                .setMessage("${c.name} silinsin mi?")
                .setPositiveButton("Evet") { _, _ ->
                    val pos = h.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        channels.removeAt(pos)
                        notifyItemRemoved(pos)
                    }
                }
                .setNegativeButton("Hayır", null)
                .show()
            true
        }
    }

    override fun getItemCount() = channels.size
}
