package com.turkbox.tv

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CustomChannelAdapter
    private var channelList = mutableListOf<Channel>()
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Örnek Kanallar (Hem m3u8 hem web sayfası)
        channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))
        // Test için web sayfası ekleyebilirsin:
        // channelList.add(Channel("ATV Canlı", "https://www.atv.com.tr/canli-yayin"))

        rv = findViewById(R.id.recyclerView)
        rv.layoutManager = GridLayoutManager(this, 4)

        // Adapter kurulumu ve tıklama mantığı
        adapter = CustomChannelAdapter(channelList, {}, { channel ->
            handleChannelSelection(channel)
        })
        
        rv.adapter = adapter
    }

    private fun handleChannelSelection(channel: Channel) {
        // Eğer link zaten doğrudan m3u8 ise bekletmeden aç
        if (channel.url.contains(".m3u8")) {
            startPlayer(channel.url)
        } else {
            // Eğer web sayfasıysa (atv.com.tr gibi), linki kazımaya başla
            Toast.makeText(this, "Yayın linki aranıyor...", Toast.LENGTH_SHORT).show()
            parseWebUrl(channel.url)
        }
    }

    private fun parseWebUrl(webUrl: String) {
        // Arka planda (IO thread) çalıştırıyoruz ki uygulama donmasın
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Web sayfasının HTML içeriğini indir
                val doc = Jsoup.connect(webUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get()
                
                val htmlContent = doc.html()
                
                // HTML içinde .m3u8 ile biten linki Regex ile ara
                val regex = "(https?://[^\"]+\\.m3u8[^\"]*)".toRegex()
                val match = regex.find(htmlContent)
                
                val foundUrl = match?.value

                withContext(Dispatchers.Main) {
                    if (foundUrl != null) {
                        startPlayer(foundUrl)
                    } else {
                        Toast.makeText(this@MainActivity, "Yayın linki bulunamadı!", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Bağlantı hatası: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startPlayer(url: String) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }
}
