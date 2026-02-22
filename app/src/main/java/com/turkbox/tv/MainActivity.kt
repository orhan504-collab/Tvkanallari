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

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CustomChannelAdapter
    private var channelList = mutableListOf<Channel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = GridLayoutManager(this, 4)

        adapter = CustomChannelAdapter(channelList, {}, { channel ->
            handleChannelSelection(channel)
        })
        rv.adapter = adapter

        // Ekleme Butonu (XML ID: btnAddChannel)
        val btnAdd = findViewById<FloatingActionButton>(R.id.btnAddChannel)
        btnAdd.setOnClickListener {
            showAddDialog()
        }

        if (channelList.isEmpty()) {
            channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))
            channelList.add(Channel("ATV Canlı", "https://www.atv.com.tr/canli-yayin"))
            adapter.notifyDataSetChanged()
        }
    }

    private fun handleChannelSelection(channel: Channel) {
        if (channel.url.contains(".m3u8") || channel.url.contains(".ts")) {
            // Doğrudan yayın linki
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("url", channel.url)
            startActivity(intent)
        } else {
            // Web sayfası linki (WebView ile aç)
            val intent = Intent(this, WebPlayerActivity::class.java)
            intent.putExtra("url", channel.url)
            startActivity(intent)
        }
    }

    private fun showAddDialog() {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_channel, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etUrl = dialogView.findViewById<EditText>(R.id.etUrl)

        builder.setView(dialogView)
            .setTitle("Kanal Ekle")
            .setPositiveButton("EKLE") { _, _ ->
                val name = etName.text.toString().trim()
                val url = etUrl.text.toString().trim()
                if (name.isNotEmpty() && url.isNotEmpty()) {
                    channelList.add(Channel(name, url))
                    adapter.notifyItemInserted(channelList.size - 1)
                }
            }.setNegativeButton("İPTAL", null).show()
    }
}
