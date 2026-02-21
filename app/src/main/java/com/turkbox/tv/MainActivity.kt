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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Örnek başlangıç kanalı
        channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        
        adapter = CustomChannelAdapter(channelList, {}, { channel ->
            // Burada VideoView veya Player'ı başlatabilirsin
            Toast.makeText(this, "Açılıyor: ${channel.name}", Toast.LENGTH_SHORT).show()
        })
        recyclerView.adapter = adapter

        // KANAL EKLEME BUTONU
        findViewById<FloatingActionButton>(R.id.btnAddChannel).setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_channel, null)
            val etName = view.findViewById<EditText>(R.id.etName)
            val etUrl = view.findViewById<EditText>(R.id.etUrl)

            AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                .setView(view)
                .setPositiveButton("Ekle") { _, _ ->
                    if (etName.text.isNotEmpty() && etUrl.text.isNotEmpty()) {
                        channelList.add(Channel(etName.text.toString(), etUrl.text.toString()))
                        adapter.notifyItemInserted(channelList.size - 1)
                    }
                }.setNegativeButton("İptal", null).show()
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
