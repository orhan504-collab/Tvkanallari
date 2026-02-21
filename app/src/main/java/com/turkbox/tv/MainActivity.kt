package com.turkbox.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import org.json.JSONObject
import java.io.InputStream

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = BrowseSupportFragment()
            supportFragmentManager.beginTransaction().replace(R.id.main_frame, fragment).commit()
            
            fragment.title = "TurkBox TV - Canlı Yayınlar"
            setupChannels(fragment)
        }
    }

    private fun setupChannels(fragment: BrowseSupportFragment) {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = VerticalGridPresenter() // Basit bir liste yapısı için

        // JSON'dan kanalları oku
        val channels = loadChannelsFromJSON()
        val listRowAdapter = ArrayObjectAdapter(CardPresenter())
        channels.forEach { listRowAdapter.add(it) }

        val header = HeaderItem(0, "Tüm Kanallar")
        rowsAdapter.add(ListRow(header, listRowAdapter))
        fragment.adapter = rowsAdapter
    }

    private fun loadChannelsFromJSON(): List<Channel> {
        val channelList = mutableListOf<Channel>()
        try {
            val inputStream: InputStream = assets.open("channels.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(json)
            val jsonArray = jsonObject.getJSONArray("channels")

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                channelList.add(Channel(
                    item.getInt("id"),
                    item.getString("name"),
                    item.getString("url"),
                    item.getString("logo")
                ))
            }
        } catch (e: Exception) { e.printStackTrace() }
        return channelList
    }
}
