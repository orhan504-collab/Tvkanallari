package com.turkbox.tv

import android.content.Intent
import android.os.Bundle
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
    private var lastClickedChannelUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // XML ID'leri ile eşleştirme
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
                // Üstüne gelince sadece ismi yazsın (Gereksiz trafik ve ses kirliliğini önler)
                tvSelectedChannel.text = channel.name
            },
            onClick = { channel: Channel ->
                // EĞER AYNI KANALA 2. KEZ TIKLANIRSA VEYA ZATEN ÇERÇEVEDE OYNUYORSA TAM EKRAN YAP
                if (lastClickedChannelUrl == channel.url) {
                    openFullScreen(channel)
                } else {
                    // İLK TIKLAMADA: Sağdaki çerçeve içinde videoyu başlat
                    lastClickedChannelUrl = channel.url
                    tvSelectedChannel.text = "Oynatılıyor: ${channel.name}"
                    
                    try {
                        previewVideo.setVideoPath(channel.url)
                        previewVideo.setOnPreparedListener { mp ->
                            mp.setVolume(1.0f, 1.0f) // Sesi aç
                            mp.start()
                        }
                        previewVideo.setOnErrorListener { _, _, _ -> 
                            tvSelectedChannel.text = "Yayın açılamadı: ${channel.name}"
                            true 
                        }
                        
                        // Sağdaki video çerçevesine tıklanırsa da tam ekran yap
                        previewVideo.setOnClickListener {
                            openFullScreen(channel)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        )
        
        rvChannelList.layoutManager = LinearLayoutManager(this)
        rvChannelList.adapter = adapter
    }

    private fun openFullScreen(channel: Channel) {
        val intent = Intent(this, PlaybackActivity::class.java).apply {
            putExtra("CHANNEL_NAME", channel.name)
            putExtra("CHANNEL_URL", channel.url)
        }
        startActivity(intent)
    }
}
