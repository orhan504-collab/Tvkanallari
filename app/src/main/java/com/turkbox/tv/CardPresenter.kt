package com.turkbox.tv

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import android.graphics.Color

class CardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // HATA ÇÖZÜMÜ: Genişlik ve yüksekliği burada zorunlu olarak tanımlıyoruz
            layoutParams = ViewGroup.LayoutParams(313, 176) 
            setBackgroundColor(Color.parseColor("#202020")) // Koyu gri arka plan
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = channel.name
        cardView.contentText = "Canlı Yayın"
        // Kartın içindeki görsel alanının boyutlarını ayarlıyoruz
        cardView.setMainImageDimensions(313, 176)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
