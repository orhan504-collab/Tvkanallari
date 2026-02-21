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
        val listRowAdapter = ArrayObjectAdapter(CardPresenter())

        try {
            // Dosyayı assets'ten okumaya çalış
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
            // HATA DURUMU: Ekranda neyin yanlış olduğunu gösteren bir kart oluştur
            val hataMesaji = when(e) {
                is IOException -> "Dosya Bulunamadı (assets/channels.json)"
                else -> "JSON Hatası: ${e.message}"
            }
            listRowAdapter.add(Channel(0, hataMesaji, "", ""))
        }

        val header = HeaderItem(0, "Yayın Listesi")
        rowsAdapter.add(ListRow(header, listRowAdapter))
        fragment.adapter = rowsAdapter
    }
}
