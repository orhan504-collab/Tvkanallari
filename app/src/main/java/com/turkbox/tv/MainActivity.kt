package com.turkbox.tv

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CustomChannelAdapter
    private var channelList = mutableListOf<Channel>()
    private var previewPlayer: ExoPlayer? = null
    private lateinit var previewView: PlayerView
    
    // Tıklama hızı kontrolü (Çift tık için)
    private var lastClickTime: Long = 0
    private val doubleClickTimeout = 300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Sağdaki önizleme oynatıcısını bağla
        previewView = findViewById(R.id.previewPlayerView)
        setupPreviewPlayer()

        // 2. assets/channels.json dosyasından verileri çek
        loadChannelsFromJson()

        // 3. RecyclerView (Kanal Listesi) ayarları
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this)

        // 4. Adapter'ı tüm özelliklerle (Focus, Click, LongClick) başlat
        adapter = CustomChannelAdapter(
            channelList,
            onFocus = { channel -> playInPreview(channel.url) }, 
            onClick = { channel -> handleInteraction(channel) }, 
            onLongClick = { channel, position -> showOptionsDialog(channel, position) }
        )
        
        rv.adapter = adapter

        // 5. Kanal Ekleme Butonu
        findViewById<FloatingActionButton>(R.id.btnAddChannel).setOnClickListener { 
            showAddDialog() 
        }
    }

    private fun setupPreviewPlayer() {
        previewPlayer = ExoPlayer.Builder(this).build()
        previewView.player = previewPlayer
    }

    private fun loadChannelsFromJson() {
        try {
            val inputStream: InputStream = assets.open("channels.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                channelList.add(Channel(obj.getString("name"), obj.getString("url")))
            }
        } catch (e: Exception) {
            // JSON bulunamazsa varsayılan bir kanal ekle
            if (channelList.isEmpty()) {
                channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))
            }
        }
    }

    private fun playInPreview(url: String) {
        // Sadece doğrudan m3u8 linklerini sağdaki küçük ekranda oynat
        if (url.contains(".m3u8") || url.contains(".ts")) {
            val mediaItem = MediaItem.fromUri(url)
            previewPlayer?.setMediaItem(mediaItem)
            previewPlayer?.prepare()
            previewPlayer?.play()
        } else {
            previewPlayer?.stop() // Web sitesi linki ise önizlemeyi kapat
        }
    }

    private fun handleInteraction(channel: Channel) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < doubleClickTimeout) {
            // Çift Tıklama -> Tam Ekran Oynatıcıyı Aç
            openFullScreen(channel.url)
        } else {
            // Tek Tıklama -> Sağdaki Önizlemede Aç
            playInPreview(channel.url)
        }
        lastClickTime = currentTime
    }

    private fun openFullScreen(url: String) {
        previewPlayer?.pause()
        val intent = if (url.contains(".m3u8") || url.contains(".ts")) {
            Intent(this, PlayerActivity::class.java)
        } else {
            Intent(this, WebPlayerActivity::class.java)
        }
        intent.putExtra("url", url)
        startActivity(intent)
    }

    private fun showOptionsDialog(channel: Channel, position: Int) {
        val options = arrayOf("Düzenle", "Sil")
        AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle(channel.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(channel, position)
                    1 -> {
                        channelList.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        Toast.makeText(this, "Kanal silindi", Toast.LENGTH_SHORT).show()
                    }
                }
            }.show()
    }

    private fun showEditDialog(channel: Channel, position: Int) {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_channel, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etUrl = dialogView.findViewById<EditText>(R.id.etUrl)

        etName.setText(channel.name)
        etUrl.setText(channel.url)

        builder.setView(dialogView).setTitle("Kanalı Düzenle")
            .setPositiveButton("GÜNCELLE") { _, _ ->
                channel.name = etName.text.toString()
                channel.url = etUrl.text.toString()
                adapter.notifyItemChanged(position)
                Toast.makeText(this, "Güncellendi", Toast.LENGTH_SHORT).show()
            }.setNegativeButton("İPTAL", null).show()
    }

    private fun showAddDialog() {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_channel, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etUrl = dialogView.findViewById<EditText>(R.id.etUrl)

        builder.setView(dialogView).setTitle("Yeni Kanal Ekle")
            .setPositiveButton("EKLE") { _, _ ->
                val name = etName.text.toString().trim()
                val url = etUrl.text.toString().trim()
                if (name.isNotEmpty() && url.isNotEmpty()) {
                    channelList.add(Channel(name, url))
                    adapter.notifyItemInserted(channelList.size - 1)
                }
            }.setNegativeButton("İPTAL", null).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        previewPlayer?.release()
    }
}
