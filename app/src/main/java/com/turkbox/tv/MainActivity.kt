package com.turkbox.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = BrowseSupportFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, fragment)
            .commit()

        fragment.title = "TurkBox TV"
        setupAdapter(fragment)
    }

    private fun setupAdapter(fragment: BrowseSupportFragment) {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)

        try {
            // Assets klasöründen dosyayı oku
            val jsonString = assets.open("channels.json").bufferedReader().use { it.readText() }
            
            // JSON paketini aç ve "channels" listesini al
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray("channels")

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                listRowAdapter.add(Channel(
                    obj.optInt("id", i),
                    obj.getString("name"),
                    obj.getString("url"),
                    obj.optString("logo", "")
                ))
            }
        } catch (e: Exception) {
            // Hata mesajını ekrana basan bir kart oluştur
            listRowAdapter.add(Channel(0, "Veri Hatası: ${e.localizedMessage}", "", ""))
        }

        val header = HeaderItem(0, "Canlı Yayınlar")
        rowsAdapter.add(ListRow(header, listRowAdapter))
        fragment.adapter = rowsAdapter
    }
}
