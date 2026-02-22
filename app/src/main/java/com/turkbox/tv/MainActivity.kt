package com.turkbox.tv

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CustomChannelAdapter
    private var channelList = mutableListOf<Channel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerView Kurulumu (4'lü Izgara)
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = GridLayoutManager(this, 4)

        adapter = CustomChannelAdapter(channelList, {}, { channel ->
            if (channel.url.contains(".m3u8")) {
                startPlayer(channel.url)
            } else {
                Toast.makeText(this, "Yayın aranıyor...", Toast.LENGTH_SHORT).show()
                parseWebAndPlay(channel.url)
            }
        })
        rv.adapter = adapter

        // XML ID'Sİ 'btnAddChannel' OLARAK GÜNCELLENDİ
        val btnAdd = findViewById<FloatingActionButton>(R.id.btnAddChannel)
        btnAdd.setOnClickListener {
            showAddDialog()
        }

        // Başlangıç Kanalları
        if (channelList.isEmpty()) {
            channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))
            adapter.notifyDataSetChanged()
        }
    }

    private fun showAddDialog() {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_channel, null)
        
        // dialog_add_channel içindeki etName ve etUrl ID'leri
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etUrl = dialogView.findViewById<EditText>(R.id.etUrl)

        builder.setView(dialogView)
            .setTitle("Yeni Kanal")
            .setPositiveButton("EKLE") { _, _ ->
                val name = etName.text.toString().trim()
                val url = etUrl.text.toString().trim()
                if (name.isNotEmpty() && url.isNotEmpty()) {
                    channelList.add(Channel(name, url))
                    adapter.notifyItemInserted(channelList.size - 1)
                } else {
                    Toast.makeText(this, "Lütfen alanları doldurun!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("İPTAL", null)
            .show()
    }

    private fun parseWebAndPlay(webUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val doc = Jsoup.connect(webUrl).userAgent("Mozilla/5.0").timeout(10000).get()
                val regex = "(https?://[^\"]+\\.m3u8[^\"]*)".toRegex()
                val foundUrl = regex.find(doc.html())?.value

                withContext(Dispatchers.Main) {
                    if (foundUrl != null) {
                        startPlayer(foundUrl)
                    } else {
                        Toast.makeText(this@MainActivity, "Web sayfasında link bulunamadı!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Bağlantı hatası!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startPlayer(url: String) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }
}
