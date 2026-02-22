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
    
    private var inputBuffer = StringBuilder()
    private val handler = Handler(Looper.getMainLooper())
    private val jumpRunnable = Runnable { processChannelJump() }
    
    private var lastClickTime: Long = 0
    private val doubleClickTimeout = 300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewPlayerView)
        setupPreviewPlayer()
        
        loadChannelsWithIds()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CustomChannelAdapter(
            channelList,
            onFocus = { channel -> playInPreview(channel.url) },
            onClick = { channel -> handleInteraction(channel) },
            onLongClick = { channel, position -> showOptionsDialog(channel, position) }
        )
        
        recyclerView.adapter = adapter
        setupDragAndDrop()

        findViewById<FloatingActionButton>(R.id.btnAddChannel).setOnClickListener { showAddDialog() }
    }

    private fun setupPreviewPlayer() {
        previewPlayer = ExoPlayer.Builder(this).build()
        previewView.player = previewPlayer
    }

    private fun loadChannelsWithIds() {
        try {
            val inputStream: InputStream = assets.open("channels.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)
            channelList.clear()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                // Hata almamak için parametre isimlerini açıkça belirtiyoruz
                channelList.add(Channel(
                    name = obj.getString("name"),
                    url = obj.getString("url"),
                    id = i + 1
                ))
            }
        } catch (e: Exception) {
            if (channelList.isEmpty()) {
                channelList.add(Channel(name = "TRT 1", url = "https://trt.daioncdn.net/trt-1/master.m3u8?app=web", id = 1))
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            val digit = keyCode - KeyEvent.KEYCODE_0
            inputBuffer.append(digit)
            
            handler.removeCallbacks(jumpRunnable)
            handler.postDelayed(jumpRunnable, 1200)
            
            Toast.makeText(this, "Kanal: $inputBuffer", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun processChannelJump() {
        val targetNo = inputBuffer.toString().toIntOrNull()
        inputBuffer.setLength(0)

        if (targetNo != null) {
            val index = channelList.indexOfFirst { it.id == targetNo }
            if (index != -1) {
                recyclerView.scrollToPosition(index)
                recyclerView.postDelayed({
                    val vh = recyclerView.findViewHolderForAdapterPosition(index)
                    vh?.itemView?.requestFocus()
                    playInPreview(channelList[index].url)
                }, 200)
            } else {
                Toast.makeText(this, "Kanal $targetNo bulunamadı", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDragAndDrop() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val from = vh.adapterPosition
                val to = target.adapterPosition
                Collections.swap(channelList, from, to)
                reassignChannelIds()
                adapter.notifyItemMoved(from, to)
                adapter.notifyItemRangeChanged(0, channelList.size)
                return true
            }
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun reassignChannelIds() {
        for (i in 0 until channelList.size) {
            channelList[i].id = i + 1
        }
    }

    private fun playInPreview(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        previewPlayer?.setMediaItem(mediaItem)
        previewPlayer?.prepare()
        previewPlayer?.play()
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
        val intent = Intent(this, PlayerActivity::class.java)
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
                    1 -> deleteChannel(position)
                }
            }.show()
    }

    private fun deleteChannel(position: Int) {
        channelList.removeAt(position)
        reassignChannelIds() // Numaraları 1, 2, 3 diye yeniden düzenle
        adapter.notifyDataSetChanged()
        Toast.makeText(this, "Kanal silindi", Toast.LENGTH_SHORT).show()
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
                    channelList.add(Channel(name = name, url = url, id = channelList.size + 1))
                    adapter.notifyItemInserted(channelList.size - 1)
                }
            }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        previewPlayer?.release()
    }
}
