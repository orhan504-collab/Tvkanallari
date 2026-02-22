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
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerView Hazırlığı
        rv = findViewById(R.id.recyclerView)
        rv.layoutManager = GridLayoutManager(this, 4)

        adapter = CustomChannelAdapter(channelList, {}, { channel ->
            handleChannelSelection(channel)
        })
        rv.adapter = adapter

        // Ekleme Butonu (XML'deki ID'nizin 'fabAddChannel' olduğunu varsayıyorum)
        val fab = findViewById<FloatingActionButton>(R.id.fabAddChannel)
        fab.setOnClickListener {
            showAddChannelDialog()
        }

        // Başlangıç kanalı
        channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))
        adapter.notifyDataSetChanged()
    }

    private fun showAddChannelDialog() {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add_channel, null)
        
        // SENİN XML ID'LERİNLE EŞLEŞTİRİLDİ:
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etUrl = dialogView.findViewById<EditText>(R.id.etUrl)

        builder.setView(dialogView)
            .setPositiveButton("EKLE") { _, _ ->
                val name = etName.text.toString().trim()
                val url = etUrl.text.toString().trim()
                
                if (name.isNotEmpty() && url.isNotEmpty()) {
                    channelList.add(Channel(name, url))
                    adapter.notifyItemInserted(channelList.size - 1)
                    Toast.makeText(this, "$name eklendi", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Alanları doldurun!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("İPTAL", null)
            .show()
    }

    private fun handleChannelSelection(channel: Channel) {
        if (channel.url.contains(".m3u8")) {
            startPlayer(channel.url)
        } else {
            Toast.makeText(this, "Yayın aranıyor...", Toast.LENGTH_SHORT).show()
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
                
                val htmlContent = doc.html()
                val regex = "(https?://[^\"]+\\.m3u8[^\"]*)".toRegex()
                val match = regex.find(htmlContent)
                val foundUrl = match?.value

                withContext(Dispatchers.Main) {
                    if (foundUrl != null) {
                        startPlayer(foundUrl)
                    } else {
                        Toast.makeText(this@MainActivity, "Yayın linki bulunamadı!", Toast.LENGTH_LONG).show()
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
