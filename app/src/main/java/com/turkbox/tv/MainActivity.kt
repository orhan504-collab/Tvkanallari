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
                // Kanal üstüne gelince önizleme yap
                tvSelectedChannel.text = channel.name
                try {
                    previewVideo.setVideoPath(channel.url)
                    previewVideo.setOnPreparedListener { mp ->
                        mp.setVolume(0f, 0f) // Sessiz önizleme
                        mp.start()
                    }
                    previewVideo.setOnErrorListener { _, _, _ -> true }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            onClick = { channel: Channel ->
                // Tıklayınca tam ekran oynatıcıya git
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
