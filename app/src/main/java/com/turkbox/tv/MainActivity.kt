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
            // Assets klasöründeki dosyayı güvenli açma
            val inputStream = assets.open("channels.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray("channels")

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
            // Hata olursa boş kalmasın, hata mesajını kart olarak gösterelim
            listRowAdapter.add(Channel(0, "Hata: ${e.message}", "", ""))
        }

        val header = HeaderItem(0, "Canlı Yayınlar")
        rowsAdapter.add(ListRow(header, listRowAdapter))
        fragment.adapter = rowsAdapter
    }
}
