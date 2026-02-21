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
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: CustomChannelAdapter
    private var channelList = mutableListOf<Channel>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Başlangıç kanalı - Sadece isim ve URL
        channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        
        adapter = CustomChannelAdapter(channelList, 
            onFocus = { channel -> /* Odaklanma işlemi */ },
            onClick = { channel -> 
                Toast.makeText(this, "Açılıyor: ${channel.name}", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter

        // KANAL EKLEME BUTONU
        val btnAdd = findViewById<FloatingActionButton>(R.id.btnAddChannel)
        btnAdd.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_channel, null)
            val etName = view.findViewById<EditText>(R.id.etName)
            val etUrl = view.findViewById<EditText>(R.id.etUrl)

            AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                .setTitle("Yeni Kanal")
                .setView(view)
                .setPositiveButton("Ekle") { _, _ ->
                    val name = etName.text.toString()
                    val url = etUrl.text.toString()
                    if (name.isNotEmpty() && url.isNotEmpty()) {
                        channelList.add(Channel(name, url))
                        adapter.notifyItemInserted(channelList.size - 1)
                    }
                }
                .setNegativeButton("İptal", null)
                .show()
        }

        // SÜRÜKLE BIRAK (TAŞIMA)
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
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
