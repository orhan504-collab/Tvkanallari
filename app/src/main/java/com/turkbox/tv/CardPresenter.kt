package com.turkbox.tv

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import android.graphics.Color
import android.view.ContextThemeWrapper

class CardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        // Hata buradaydı: Temayı ve genişliği garantiye alıyoruz
        val cardView = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // Genişlik ve yükseklik değerlerini açıkça belirtiyoruz
            layoutParams = ViewGroup.LayoutParams(400, 250) 
            setBackgroundColor(Color.DKGRAY)
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = channel.name
        cardView.contentText = "Canlı Yayın"
        // Görsel alanını kartın içine sığdırıyoruz
        cardView.setMainImageDimensions(400, 225)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
