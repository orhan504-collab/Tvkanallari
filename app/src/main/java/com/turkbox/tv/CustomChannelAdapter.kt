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
        val card: CardView = v as CardView
        val name: TextView = v.findViewById(R.id.tvChannelName)
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int) = ViewHolder(
        LayoutInflater.from(p.context).inflate(R.layout.item_channel, p, false)
    )

    override fun onBindViewHolder(h: ViewHolder, p: Int) {
        val c = channels[p]
        h.name.text = c.name

        h.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(150).start()
                h.card.setCardBackgroundColor(Color.parseColor("#E50914"))
                onFocus(c)
            } else {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
                h.card.setCardBackgroundColor(Color.parseColor("#222222"))
            }
        }

        h.itemView.setOnClickListener { onClick(c) }

        h.itemView.setOnLongClickListener {
            AlertDialog.Builder(h.itemView.context, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                .setTitle("Kanalı Sil")
                .setMessage("${c.name} silinsin mi?")
                .setPositiveButton("Sil") { _, _ ->
                    val pos = h.adapterPosition
                    channels.removeAt(pos)
                    notifyItemRemoved(pos)
                }
                .setNegativeButton("İptal", null).show()
            true
        }
    }

    override fun getItemCount() = channels.size
}
