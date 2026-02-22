package com.turkbox.tv

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CustomChannelAdapter
    private var channelList = mutableListOf<Channel>()
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))

        rv = findViewById(R.id.recyclerView)
        rv.layoutManager = GridLayoutManager(this, 4)

        // PlayerActivity hatası burada giderildi
        adapter = CustomChannelAdapter(channelList, {}, { channel ->
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("url", channel.url)
            startActivity(intent)
        })
        rv.adapter = adapter

        findViewById<FloatingActionButton>(R.id.btnAddChannel).setOnClickListener {
            showAddDialog()
        }

        setupItemTouchHelper()
    }

    private fun showAddDialog() {
        val v = LayoutInflater.from(this).inflate(R.layout.dialog_add_channel, null)
        val etName = v.findViewById<EditText>(R.id.etName)
        val etUrl = v.findViewById<EditText>(R.id.etUrl)

        AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Kanal Ekle")
            .setView(v)
            .setPositiveButton("Ekle") { _, _ ->
                val name = etName.text.toString()
                val url = etUrl.text.toString()
                if (url.contains(".m3u8")) {
                    channelList.add(Channel(name, url))
                    adapter.notifyItemInserted(channelList.size - 1)
                } else {
                    findM3u8(name, url)
                }
            }.show()
    }

    private fun findM3u8(name: String, webUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val doc = Jsoup.connect(webUrl).userAgent("Mozilla/5.0").get()
                val match = "(https?://[^\\s\"']+\\.m3u8[^\\s\"']*)".toRegex().find(doc.toString())
                withContext(Dispatchers.Main) {
                    channelList.add(Channel(name, match?.value ?: webUrl))
                    adapter.notifyItemInserted(channelList.size - 1)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Link bulunamadı", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupItemTouchHelper() {
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean {
                val from = vh.adapterPosition
                val to = t.adapterPosition
                Collections.swap(channelList, from, to)
                adapter.notifyItemMoved(from, to)
                return true
            }
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {}
        })
        helper.attachToRecyclerView(rv)
    }
}
