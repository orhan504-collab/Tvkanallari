package com.turkbox.tv

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ekranın kararmasını engelle
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val playerView = PlayerView(this)
        setContentView(playerView)

        // MainActivity'den gelen URL'yi alıyoruz
        val videoUrl = intent.getStringExtra("url") 
        
        if (videoUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Video URL bulunamadı!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        player = ExoPlayer.Builder(this).build().apply {
            playerView.player = this
            
            // Hata dinleyici: Yayın neden açılmıyor görmeni sağlar
            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Toast.makeText(this@PlayerActivity, "Yayın Hatası: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })

            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    // Uygulama durduğunda belleği boşaltalım (TV uygulamalarında çok kritiktir)
    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
