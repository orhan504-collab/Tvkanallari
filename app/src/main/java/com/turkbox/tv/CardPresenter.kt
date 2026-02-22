package com.turkbox.tv

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter

class CardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val textView = TextView(parent.context).apply {
            // Boyutları biraz daha TV dostu yapalım
            layoutParams = ViewGroup.LayoutParams(320, 180) 
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(Color.parseColor("#444444")) // Biraz daha yumuşak bir gri
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            textSize = 18f // Yazı boyutunu netleştirelim
            elevation = 10f // Hafif bir gölge derinliği
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val textView = viewHolder.view as TextView
        textView.text = channel.name
        
        textView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // Odaklandığında kırmızı ve hafif büyük görünmesi için
                v.setBackgroundColor(Color.parseColor("#E50914")) // Netflix kırmızısına yakın
                v.scaleX = 1.1f
                v.scaleY = 1.1f
            } else {
                v.setBackgroundColor(Color.parseColor("#444444"))
                v.scaleX = 1.0f
                v.scaleY = 1.0f
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Kaynakları temizlemek istersen burayı kullanabilirsin
    }
}
