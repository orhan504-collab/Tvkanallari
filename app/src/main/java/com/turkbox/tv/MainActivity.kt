package com.turkbox.tv

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class MainActivity : FragmentActivity() {

    private lateinit var rvChannelList: RecyclerView
    private lateinit var previewVideo: VideoView
    private lateinit var tvSelectedChannel: TextView
    private val channelList = mutableListOf<Channel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // XML'deki ID'lerle eşleştirme
        rvChannelList = findViewById(R.id.rvChannelList)
        previewVideo = findViewById(R.id.previewVideoView)
        tvSelectedChannel = findViewById(R.id.tvSelectedChannel)

        loadChannels()
        setupRecyclerView()
    }

    private fun loadChannels() {
        try {
            val jsonString = assets.open("channels.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray("channels")

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                channelList.add(Channel(
                    id = obj.optInt("id", i),
                    name = obj.getString("name"),
                    url = obj.getString("url"),
                    logo = obj.optString("logo", "")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupRecyclerView() {
        val adapter = CustomChannelAdapter(
            channelList,
            onFocus = { channel: Channel ->
                // Kanalın üzerine gelince sağda önizleme başlasın
                tvSelectedChannel.text = channel.name
                try {
                    previewVideo.setVideoPath(channel.url)
                    previewVideo.setOnPreparedListener { mp ->
                        mp.setVolume(0f, 0f) // Önizleme sessiz
                        mp.start()
                    }
                    previewVideo.setOnErrorListener { _, _, _ ->
                        true // Hata durumunda uygulamayı çökertme
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            onClick = { channel: Channel ->
                // Tıklayınca Tam Ekran oynatıcıya git
                val intent = Intent(this, PlaybackActivity::class.java).apply {
                    putExtra("CHANNEL_NAME", channel.name)
                    putExtra("CHANNEL_URL", channel.url)
                }
                startActivity(intent)
            }
        )
        
        rvChannelList.layoutManager = LinearLayoutManager(this)
        rvChannelList.adapter = adapter
    }
}

// --- YARDIMCI SINIFLAR (Aynı dosya içinde veya ayrı dosyalarda olabilir) ---

// 1. Kanal Veri Yapısı
data class Channel(
    val id: Int,
    val name: String,
    val url: String,
    val logo: String
)

// 2. Liste Adaptörü (RecyclerView için)
class CustomChannelAdapter(
    private val channels: List<Channel>,
    private val onFocus: (Channel) -> Unit,
    private val onClick: (Channel) -> Unit
) : RecyclerView.Adapter<CustomChannelAdapter.ChannelViewHolder>() {

    class ChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channels[position]
        holder.tvName.text = channel.name
        holder.tvName.setTextColor(Color.WHITE)
        holder.itemView.isFocusable = true
        holder.itemView.isFocusableInTouchMode = true

        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.itemView.setBackgroundColor(Color.parseColor("#FF0000")) // Odaklanınca Kırmızı
                onFocus(channel)
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        holder.itemView.setOnClickListener {
            onClick(channel)
        }
    }

    override fun getItemCount() = channels.size
}
