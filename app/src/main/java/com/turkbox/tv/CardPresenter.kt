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
            setBackgroundColor(Color.DKGRAY) // Görünür olması için renk ekledik
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = channel.name
        cardView.contentText = "Kanalı İzle"
        cardView.setMainImageDimensions(313, 176)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
