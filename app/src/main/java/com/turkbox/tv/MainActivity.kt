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

        // Fragment'ı güvenli bir şekilde başlatıyoruz
        val fragment = supportFragmentManager.findFragmentById(R.id.main_browse_fragment) as? BrowseSupportFragment 
            ?: BrowseSupportFragment()

        if (!fragment.isAdded) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit()
        }
        
        fragment.title = "TurkBox TV"
        setupAdapter(fragment)
    }

    private fun setupAdapter(fragment: BrowseSupportFragment) {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val listRowAdapter = ArrayObjectAdapter(CardPresenter())

        try {
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
        } catch (e: Exception) {
            // Eğer JSON okunamazsa uygulama çökmesin diye boş bir kanal ekleyelim
            listRowAdapter.add(Channel(0, "Kanal Listesi Yüklenemedi", "", ""))
        }

        rowsAdapter.add(ListRow(HeaderItem(0, "Kanallar"), listRowAdapter))
        fragment.adapter = rowsAdapter
    }
}
