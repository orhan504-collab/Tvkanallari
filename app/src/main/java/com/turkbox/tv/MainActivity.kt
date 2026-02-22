package com.turkbox.tv

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import java.io.InputStream
import java.util.Collections

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CustomChannelAdapter
    private var channelList = mutableListOf<Channel>()
    private var previewPlayer: ExoPlayer? = null
    private lateinit var previewView: PlayerView
    private lateinit var recyclerView: RecyclerView
    
    // Çift tıklama ve Kanal No kontrolü için
    private var lastClickTime: Long = 0
    private val doubleClickTimeout = 300L
    private var inputNumber = ""
    private val handler = Handler(Looper.getMainLooper())
    private val inputRunnable = Runnable { processChannelNumber() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewPlayerView)
        setupPreviewPlayer()
        loadChannelsFromJson()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CustomChannelAdapter(
            channelList,
            onFocus = { channel -> playInPreview(channel.url) },
            onClick = { channel -> handleInteraction(channel) },
            onLongClick = { channel, position -> showOptionsDialog(channel, position) }
        )
        
        recyclerView.adapter = adapter

        // 1. ÖZELLİK: SÜRÜKLE-BIRAK SIRALAMA
        setupDragAndDrop()

        findViewById<FloatingActionButton>(R.id.btnAddChannel).setOnClickListener { showAddDialog() }
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
            channelList.clear()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                channelList.add(Channel(obj.getString("name"), obj.getString("url")))
            }
        } catch (e: Exception) {
            if (channelList.isEmpty()) {
                channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))
            }
        }
    }

    // 2. ÖZELLİK: KUMANDA RAKAM TUŞLARI DİNAMİĞİ
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            val number = keyCode - KeyEvent.KEYCODE_0
            inputNumber += number.toString()
            
            handler.removeCallbacks(inputRunnable)
            handler.postDelayed(inputRunnable, 1500) // 1.5 saniye sonra kanala git
            
            Toast.makeText(this, "Kanal: $inputNumber", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun processChannelNumber() {
        val channelIdx = inputNumber.toIntOrNull()?.minus(1) 
        if (channelIdx != null && channelIdx >= 0 && channelIdx < channelList.size) {
            val targetChannel = channelList[channelIdx]
            recyclerView.scrollToPosition(channelIdx)
            // İlgili satırı seçili hale getirmek için odakla
            recyclerView.postDelayed({
                recyclerView.findViewHolderForAdapterPosition(channelIdx)?.itemView?.requestFocus()
            }, 100)
            playInPreview(targetChannel.url)
        } else {
            Toast.makeText(this, "Kanal bulunamadı", Toast.LENGTH_SHORT).show()
        }
        inputNumber = ""
    }

    private fun setupDragAndDrop() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPos = vh.adapterPosition
                val toPos = target.adapterPosition
                Collections.swap(channelList, fromPos, toPos)
                adapter.notifyItemMoved(fromPos, toPos)
                return true
            }
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun playInPreview(url: String) {
        if (url.contains(".m3u8") || url.contains(".ts")) {
            val mediaItem = MediaItem.fromUri(url)
            previewPlayer?.setMediaItem(mediaItem)
            previewPlayer?.prepare()
            previewPlayer?.play()
        } else {
            previewPlayer?.stop()
        }
    }

    private fun handleInteraction(channel: Channel) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < doubleClickTimeout) {
            openFullScreen(channel.url)
        } else {
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

        builder.setView(dialogView).setTitle("Düzenle")
            .setPositiveButton("GÜNCELLE") { _, _ ->
                channel.name = etName.text.toString()
                channel.url = etUrl.text.toString()
                adapter.notifyItemChanged(position)
            }.show()
    }

    private fun showAddDialog() {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_channel, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etUrl = dialogView.findViewById<EditText>(R.id.etUrl)

        builder.setView(dialogView).setTitle("Yeni Kanal")
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
