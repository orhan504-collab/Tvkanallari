package com.turkbox.tv

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.util.*

class MainActivity : FragmentActivity() {

    private lateinit var rvChannelList: RecyclerView
    private lateinit var previewVideo: VideoView
    private lateinit var tvSelectedChannel: TextView
    private lateinit var videoLoader: ProgressBar
    private val channelList = mutableListOf<Channel>()
    private var lastClickedChannelUrl: String? = null
    private val SPEECH_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ekranı yatay moda zorla
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_main)

        rvChannelList = findViewById(R.id.rvChannelList)
        previewVideo = findViewById(R.id.previewVideoView)
        tvSelectedChannel = findViewById(R.id.tvSelectedChannel)
        videoLoader = findViewById(R.id.videoLoader)

        loadChannels()
        setupRecyclerView()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Kumandadaki Sesli Arama veya Arama tuşuna basıldığında
        if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_VOICE_ASSIST) {
            startVoiceSearch()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun startVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Hangi kanalı açmak istersiniz?")
        }
        try {
            startActivityForResult(intent, SPEECH_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "Sesli arama sistemi bulunamadı.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)?.lowercase(Locale("tr")) ?: ""
            val foundChannel = channelList.find { it.name.lowercase(Locale("tr")).contains(spokenText) }
            
            if (foundChannel != null) {
                val index = channelList.indexOf(foundChannel)
                rvChannelList.smoothScrollToPosition(index)
                rvChannelList.postDelayed({
                    rvChannelList.findViewHolderForAdapterPosition(index)?.itemView?.requestFocus()
                    playPreview(foundChannel)
                }, 500)
            } else {
                Toast.makeText(this, "'$spokenText' isimli kanal bulunamadı.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadChannels() {
        try {
            val json = assets.open("channels.json").bufferedReader().use { it.readText() }
            val array = JSONObject(json).getJSONArray("channels")
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                channelList.add(Channel(o.optInt("id", i), o.getString("name"), o.getString("url"), o.optString("logo", "")))
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun setupRecyclerView() {
        val adapter = CustomChannelAdapter(channelList,
            onFocus = { channel -> tvSelectedChannel.text = channel.name },
            onClick = { channel ->
                if (lastClickedChannelUrl == channel.url) {
                    val intent = Intent(this, PlaybackActivity::class.java).apply {
                        putExtra("CHANNEL_NAME", channel.name)
                        putExtra("CHANNEL_URL", channel.url)
                    }
                    startActivity(intent)
                } else {
                    playPreview(channel)
                }
            }
        )
        rvChannelList.layoutManager = LinearLayoutManager(this)
        rvChannelList.adapter = adapter
    }

    private fun playPreview(channel: Channel) {
        lastClickedChannelUrl = channel.url
        videoLoader.visibility = View.VISIBLE
        previewVideo.setVideoPath(channel.url)
        previewVideo.setOnPreparedListener { mp ->
            videoLoader.visibility = View.GONE
            mp.setVolume(1.0f, 1.0f)
            mp.start()
        }
        previewVideo.setOnErrorListener { _, _, _ ->
            videoLoader.visibility = View.GONE
            Toast.makeText(this, "Yayın yüklenemedi!", Toast.LENGTH_SHORT).show()
            true
        }
    }
}
