package com.turkbox.tv

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

class PlaybackActivity : FragmentActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)

        val playerView = findViewById<StyledPlayerView>(R.id.player_view)
        val videoUrl = intent.getStringExtra("CHANNEL_URL") ?: ""

        player = ExoPlayer.Builder(this).build().apply {
            playerView.player = this
            
            // Hata Dinleyici Ekle
            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Toast.makeText(this@PlaybackActivity, "Yayın Hatası: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })

            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
