package com.turkbox.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import org.json.JSONObject

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = BrowseSupportFragment()
        supportFragmentManager.beginTransaction().replace(R.id.main_frame, fragment).commit()
        
        fragment.title = "TurkBox TV"
        fragment.headersState = BrowseSupportFragment.HEADERS_ENABLED
        
        setupAdapter(fragment)
    }

    private fun setupAdapter(fragment: BrowseSupportFragment) {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)

        // Assets içindeki JSON'ı oku
        val jsonString = assets.open("channels.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONObject(jsonString).getJSONArray("channels")

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            listRowAdapter.add(Channel(
                obj.getInt("id"),
                obj.getString("name"),
                obj.getString("url"),
                obj.getString("logo")
            ))
        }

        val header = HeaderItem(0, "Canlı Kanallar")
        rowsAdapter.add(ListRow(header, listRowAdapter))
        fragment.adapter = rowsAdapter
    }
}
