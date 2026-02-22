package com.turkbox.tv

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CustomChannelAdapter
    private var channelList = mutableListOf<Channel>()
    private var previewPlayer: ExoPlayer? = null
    private lateinit var previewView: PlayerView
    
    // Çift tıklama kontrolü için
    private var lastClickTime: Long = 0
    private val doubleClickTimeout = 300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewPlayerView)
        setupPreviewPlayer()

        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this) // Alt alta dizilim

        // Adapter Yapılandırması
        adapter = CustomChannelAdapter(channelList, { channel ->
            // KUMANDA İLE ÜSTÜNE GELİNDİĞİNDE (FOCUS)
            playInPreview(channel.url)
        }, { channel ->
            // TIKLAMA MANTIĞI (TELEFON VE KUMANDA)
            handleInteraction(channel)
        })
        
        rv.adapter = adapter

        findViewById<FloatingActionButton>(R.id.btnAddChannel).setOnClickListener { showAddDialog() }

        // Örnek Kanallar
        if (channelList.isEmpty()) {
            channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))
            channelList.add(Channel("ATV", "https://www.atv.com.tr/canli-yayin"))
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupPreviewPlayer() {
        previewPlayer = ExoPlayer.Builder(this).build()
        previewView.player = previewPlayer
    }

    private fun playInPreview(url: String) {
        if (url.contains(".m3u8")) {
            val mediaItem = MediaItem.fromUri(url)
            previewPlayer?.setMediaItem(mediaItem)
            previewPlayer?.prepare()
            previewPlayer?.play()
        }
    }

    private fun handleInteraction(channel: Channel) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < doubleClickTimeout) {
            // ÇİFT TIKLAMA VEYA KUMANDA İLE SEÇİM -> TAM EKRAN
            openFullScreen(channel.url)
        } else {
            // TEK TIKLAMA -> SAĞDA ÖNİZLEME
            playInPreview(channel.url)
        }
        lastClickTime = currentTime
    }

    private fun openFullScreen(url: String) {
        previewPlayer?.pause()
        val intent = if (url.contains(".m3u8")) {
            Intent(this, PlayerActivity::class.java)
        } else {
            Intent(this, WebPlayerActivity::class.java)
        }
        intent.putExtra("url", url)
        startActivity(intent)
    }

    private fun showAddDialog() {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_channel, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etUrl = dialogView.findViewById<EditText>(R.id.etUrl)

        builder.setView(dialogView).setTitle("Kanal Ekle")
            .setPositiveButton("EKLE") { _, _ ->
                val name = etName.text.toString().trim()
                val url = etUrl.text.toString().trim()
                if (name.isNotEmpty() && url.isNotEmpty()) {
                    channelList.add(Channel(name, url))
                    adapter.notifyItemInserted(channelList.size - 1)
                }
            }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        previewPlayer?.release()
    }
}
