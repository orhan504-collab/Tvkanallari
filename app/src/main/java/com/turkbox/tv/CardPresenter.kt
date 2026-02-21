package com.turkbox.tv

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import android.graphics.Color
import android.view.ContextThemeWrapper

class CardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        // HATA ÇÖZÜMÜ: ContextThemeWrapper ve LayoutParams ekleyerek genişlik hatasını engelliyoruz
        val context = ContextThemeWrapper(parent.context, androidx.leanback.R.style.Widget_Leanback_ImageCardView)
        val cardView = ImageCardView(context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // Telefon ekranı için sabit bir genişlik ve yükseklik veriyoruz
            layoutParams = ViewGroup.LayoutParams(400, 250) 
            setBackgroundColor(Color.parseColor("#262626"))
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = channel.name
        cardView.contentText = "Canlı Yayın"
        // Görselin kartın içine düzgün sığması için boyutlandırma
        cardView.setMainImageDimensions(400, 225)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
