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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvChannelList = findViewById(R.id.rvChannelList)
        previewVideo = findViewById(R.id.previewVideoView)
        tvSelectedChannel = findViewById(R.id.tvSelectedChannel)

        loadChannels()
        setupRecyclerView()
    }

    private fun loadChannels() {
        try {
            val jsonString = assets.open("channels.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONObject(jsonString).getJSONArray("channels")

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                channelList.add(Channel(
                    obj.optInt("id", i),
                    obj.getString("name"),
                    obj.getString("url"),
                    obj.optString("logo", "")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupRecyclerView() {
        // Not: Burada senin mevcut CardPresenter yerine basit bir Adapter kullanman gerekir.
        // Eğer elinde bir Adapter yoksa, hızlıca bir tane oluşturabiliriz.
        val adapter = CustomChannelAdapter(channelList, 
            onFocus = { channel ->
                // Üstüne gelince (Hover/Focus) sağdaki çerçevede sessiz oynat
                tvSelectedChannel.text = channel.name
                previewVideo.setVideoPath(channel.url)
                previewVideo.setOnPreparedListener { mp ->
                    mp.setVolume(0f, 0f) // Önizleme sessiz olsun
                    mp.start()
                }
            },
            onClick = { channel ->
                // Tıklayınca PlaybackActivity'ye (Tam Ekran) git
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
