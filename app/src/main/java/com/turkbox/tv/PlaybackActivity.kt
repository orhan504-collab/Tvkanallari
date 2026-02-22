package com.turkbox.tv

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.ui.StyledPlayerView

class PlaybackActivity : FragmentActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ekranın kapanmasını engelle (Canlı yayın izlerken önemli)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        setContentView(R.layout.activity_playback)

        val playerView = findViewById<StyledPlayerView>(R.id.player_view)
        // MainActivity'den gelen URL'yi alıyoruz
        val videoUrl = intent.getStringExtra("url") ?: intent.getStringExtra("CHANNEL_URL") ?: ""

        if (videoUrl.isEmpty()) {
            Toast.makeText(this, "Geçersiz URL", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializePlayer(playerView, videoUrl)
    }

    private fun initializePlayer(playerView: StyledPlayerView, url: String) {
        // HTTP Headers (Bazı yayıncılar User-Agent kontrolü yapar)
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .setAllowCrossProtocolRedirects(true)

        // HLS (m3u8) için özel kaynak oluşturucu
        val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(url))

        player = ExoPlayer.Builder(this).build().apply {
            playerView.player = this
            
            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Toast.makeText(this@PlaybackActivity, "Yayın Hatası: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })

            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
        }
    }

    // Uygulama durdurulduğunda player'ı temizle (Bellek sızıntısını önler)
    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
