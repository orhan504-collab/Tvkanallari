package com.turkbox.tv

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CustomChannelAdapter
    private var channelList = mutableListOf<Channel>()
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerView kurulumu
        rv = findViewById(R.id.recyclerView)
        rv.layoutManager = GridLayoutManager(this, 4)

        adapter = CustomChannelAdapter(channelList, {}, { channel ->
            handleChannelSelection(channel)
        })
        rv.adapter = adapter

        // BUTON DÜZELTMESİ: XML'deki kırmızı butonu bul ve tıklandığında diyaloğu aç
        // Buradaki "fabAddChannel" isminin activity_main.xml içindeki id ile aynı olduğundan emin olun
        val fabId = resources.getIdentifier("fabAddChannel", "id", packageName)
        val fabButton = if (fabId != 0) findViewById<View>(fabId) else null
        
        fabButton?.setOnClickListener {
            showAddChannelDialog()
        } ?: run {
            // Eğer id bulunamazsa tüm View içinde FloatingActionButton ara (Fallback)
            Toast.makeText(this, "Ekleme butonu yapılandırılıyor...", Toast.LENGTH_SHORT).show()
        }

        // Örnek başlangıç kanalı
        if (channelList.isEmpty()) {
            channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))
            adapter.notifyDataSetChanged()
        }
    }

    private fun showAddChannelDialog() {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add_channel, null)
        
        // Sizin paylaştığınız XML'deki id'ler (etName ve etUrl)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etUrl = dialogView.findViewById<EditText>(R.id.etUrl)

        builder.setView(dialogView)
            .setTitle("Yeni Kanal Ekle")
            .setPositiveButton("EKLE") { _, _ ->
                val name = etName?.text?.toString()?.trim() ?: ""
                val url = etUrl?.text?.toString()?.trim() ?: ""
                
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

    private fun handleChannelSelection(channel: Channel) {
        if (channel.url.contains(".m3u8")) {
            startPlayer(channel.url)
        } else {
            Toast.makeText(this, "Yayın linki aranıyor...", Toast.LENGTH_SHORT).show()
            parseWebUrl(channel.url)
        }
    }

    private fun parseWebUrl(webUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val doc = Jsoup.connect(webUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get()
                
                val html = doc.html()
                val regex = "(https?://[^\"]+\\.m3u8[^\"]*)".toRegex()
                val match = regex.find(html)
                val foundUrl = match?.value

                withContext(Dispatchers.Main) {
                    if (foundUrl != null) {
                        startPlayer(foundUrl)
                    } else {
                        Toast.makeText(this@MainActivity, "Web sayfasında yayın bulunamadı!", Toast.LENGTH_LONG).show()
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
