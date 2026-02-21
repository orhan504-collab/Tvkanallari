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

        // Sadece ilk açılışta fragment ekle
        if (savedInstanceState == null) {
            val browseFragment = BrowseSupportFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, browseFragment)
                .commit()
            
            browseFragment.title = "TurkBox TV"
            setupAdapter(browseFragment)
        }
    }

    private fun setupAdapter(fragment: BrowseSupportFragment) {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val listRowAdapter = ArrayObjectAdapter(CardPresenter())

        try {
            // Assets klasöründeki json'ı oku
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
            // Hata olursa en azından bir uyarı kartı göster, uygulama çökmesin
            listRowAdapter.add(Channel(0, "Yükleme Hatası", "", ""))
        }

        val header = HeaderItem(0, "Canlı Yayınlar")
        rowsAdapter.add(ListRow(header, listRowAdapter))
        fragment.adapter = rowsAdapter
    }
}
