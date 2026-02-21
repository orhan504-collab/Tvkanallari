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

        val videoUrl = intent.getStringExtra("videoUrl")
        val playerView = findViewById<StyledPlayerView>(R.id.player_view)

        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        videoUrl?.let {
            val mediaItem = MediaItem.fromUri(it)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
