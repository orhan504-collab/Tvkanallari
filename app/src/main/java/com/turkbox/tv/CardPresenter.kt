package com.turkbox.tv

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter

class CardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val textView = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(350, 200)
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(Color.parseColor("#333333"))
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(10, 10, 10, 10)
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val textView = viewHolder.view as TextView
        textView.text = channel.name
        
        // Odaklandığında renk değiştirme efekti
        textView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.setBackgroundColor(Color.parseColor("#FF0000")) // Kırmızı (Odak)
            } else {
                v.setBackgroundColor(Color.parseColor("#333333")) // Gri (Normal)
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
