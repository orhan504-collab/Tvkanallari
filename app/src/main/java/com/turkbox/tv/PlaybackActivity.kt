package com.turkbox.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

class PlaybackActivity : FragmentActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)

        val playerView = findViewById<StyledPlayerView>(R.id.player_view)
        val videoUrl = intent.getStringExtra("CHANNEL_URL") ?: ""

        if (videoUrl.isNotEmpty()) {
            player = ExoPlayer.Builder(this).build().apply {
                playerView.player = this
                val mediaItem = MediaItem.fromUri(videoUrl)
                setMediaItem(mediaItem)
                prepare()
                play()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
