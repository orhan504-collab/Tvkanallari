package com.turkbox.tv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Tam ekran oynatıcı
        val playerView = PlayerView(this)
        setContentView(playerView)

        val videoUrl = intent.getStringExtra("url") ?: return

        player = ExoPlayer.Builder(this).build().apply {
            playerView.player = this
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
