package com.turkbox.tv

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter

class CardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val textView = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(300, 150)
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(Color.DKGRAY)
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
        
        textView.setOnFocusChangeListener { v, hasFocus ->
            v.setBackgroundColor(if (hasFocus) Color.RED else Color.DKGRAY)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
