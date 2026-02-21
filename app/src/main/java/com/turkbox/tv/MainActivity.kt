package com.turkbox.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Fragment'ı güvenli bir şekilde ekleyelim
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
            // Assets klasöründeki dosyayı okumaya çalışıyoruz
            val inputStream = assets.open("channels.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            
            // JSON içeriği bir liste ([]) olarak başlıyorsa bu yöntem kullanılır
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                listRowAdapter.add(Channel(
                    i,
                    obj.optString("name", "Bilinmeyen Kanal"),
                    obj.optString("url", ""),
                    obj.optString("logo", "")
                ))
            }
        } catch (e: Exception) {
            // Çökmeyi engelle: Hata mesajını kart olarak ekranda göster
            listRowAdapter.add(Channel(0, "Hata: ${e.localizedMessage}", "", ""))
        }

        val header = HeaderItem(0, "Canlı Yayınlar")
        rowsAdapter.add(ListRow(header, listRowAdapter))
        fragment.adapter = rowsAdapter
    }
}
