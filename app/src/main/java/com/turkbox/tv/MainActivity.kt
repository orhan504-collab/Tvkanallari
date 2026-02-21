package com.turkbox.tv

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
    private lateinit var rvChannels: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Başlangıç Listesi
        channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))

        rvChannels = findViewById(R.id.recyclerView)
        rvChannels.layoutManager = GridLayoutManager(this, 4)
        
        adapter = CustomChannelAdapter(channelList, {}, { channel ->
            Toast.makeText(this, "Oynatılıyor: ${channel.name}", Toast.LENGTH_SHORT).show()
        })
        rvChannels.adapter = adapter

        // ARTI (+) BUTONU - KANAL EKLEME
        findViewById<FloatingActionButton>(R.id.btnAddChannel).setOnClickListener {
            showAddChannelDialog()
        }

        // SÜRÜKLE BIRAK TAŞIMA
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0
        ) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean {
                val from = vh.adapterPosition
                val to = t.adapterPosition
                Collections.swap(channelList, from, to)
                adapter.notifyItemMoved(from, to)
                return true
            }
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(rvChannels)
    }

    private fun showAddChannelDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_channel, null)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etUrl = view.findViewById<EditText>(R.id.etUrl)

        AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Yeni Kanal Ekle")
            .setView(view)
            .setPositiveButton("Ekle") { _, _ ->
                val name = etName.text.toString()
                val url = etUrl.text.toString()
                if (name.isNotEmpty() && url.isNotEmpty()) {
                    if (url.contains(".m3u8")) {
                        // Direkt link ise hemen ekle
                        channelList.add(Channel(name, url))
                        adapter.notifyItemInserted(channelList.size - 1)
                    } else {
                        // Web sitesi ise m3u8 avla
                        findM3u8InWebsite(name, url)
                    }
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun findM3u8InWebsite(name: String, webUrl: String) {
        Toast.makeText(this, "Yayın linki aranıyor...", Toast.LENGTH_SHORT).show()
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Sayfaya bağlan ve HTML içeriğini al
                val doc = Jsoup.connect(webUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get()
                
                val html = doc.toString()
                
                // Regex: m3u8 linklerini bulur
                val regex = "(https?://[^\\s\"']+\\.m3u8[^\\s\"']*)".toRegex()
                val match = regex.find(html)
                
                val finalUrl = match?.value ?: webUrl

                withContext(Dispatchers.Main) {
                    channelList.add(Channel(name, finalUrl))
                    adapter.notifyItemInserted(channelList.size - 1)
                    rvChannels.scrollToPosition(channelList.size - 1)
                    
                    if (match != null) {
                        Toast.makeText(this@MainActivity, "M3U8 Başarıyla Yakalandı!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Otomatik link bulunamadı, girilen link eklendi.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Hata: Siteye erişilemedi.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
