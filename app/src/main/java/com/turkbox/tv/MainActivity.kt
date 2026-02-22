package com.turkbox.tv

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import java.io.InputStream
import java.util.Collections

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CustomChannelAdapter
    private var channelList = mutableListOf<Channel>()
    private var previewPlayer: ExoPlayer? = null
    private lateinit var previewView: PlayerView
    private lateinit var recyclerView: RecyclerView
    
    private var inputBuffer = StringBuilder()
    private val handler = Handler(Looper.getMainLooper())
    private val jumpRunnable = Runnable { processChannelJump() }
    
    private var lastClickTime: Long = 0
    private val doubleClickTimeout = 300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewPlayerView)
        setupPreviewPlayer()
        
        loadChannelsWithIds()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CustomChannelAdapter(
            channelList,
            onFocus = { channel -> playInPreview(channel.url) },
            onClick = { channel -> handleInteraction(channel) },
            onLongClick = { channel, position -> showOptionsDialog(channel, position) }
        )
        
        recyclerView.adapter = adapter
        setupDragAndDrop()

        findViewById<FloatingActionButton>(R.id.btnAddChannel).setOnClickListener { showAddDialog() }
    }

    private fun setupPreviewPlayer() {
        previewPlayer = ExoPlayer.Builder(this).build()
        previewView.player = previewPlayer
    }

    private fun loadChannelsWithIds() {
        try {
            val inputStream: InputStream = assets.open("channels.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)
            channelList.clear()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                // Hata almamak için parametre isimlerini açıkça belirtiyoruz
                channelList.add(Channel(
                    name = obj.getString("name"),
                    url = obj.getString("url"),
                    id = i + 1
                ))
            }
        } catch (e: Exception) {
            if (channelList.isEmpty()) {
    channelList.add(Channel(name = "Show TV", url = "https://mn-nl.mncdn.com/blutv_showtv/smil:show_sd.smil/playlist.m3u8", id = 1))
    channelList.add(Channel(name = "TRT 1", url = "https://trt.daioncdn.net/trt-1/master.m3u8?app=web", id = 2))
    channelList.add(Channel(name = "Kanal D", url = "https://demiroren.daioncdn.net/kanald/kanald.m3u8?app=kanald_web", id = 3))
    channelList.add(Channel(name = "ATV", url = "https://trkvz-live.daioncdn.net/atv/atv.m3u8", id = 4))
    channelList.add(Channel(name = "NOW TV", url = "https://uycyyuuzyh.turknet.ercdn.net/nphindgytw/nowtv/nowtv.m3u8", id = 5))
    channelList.add(Channel(name = "Star TV", url = "https://mn-nl.mncdn.com/blutv_star/smil:star_sd.smil/playlist.m3u8", id = 6))
    channelList.add(Channel(name = "TV 8", url = "https://tv8.daioncdn.net/tv8/tv8.m3u8", id = 7))
    channelList.add(Channel(name = "Kanal 7", url = "https://kanal7-live.daioncdn.net/kanal7/kanal7.m3u8", id = 8))
    channelList.add(Channel(name = "TV 100", url = "https://tv100-live.daioncdn.net/tv100/tv100.m3u8", id = 9))
    channelList.add(Channel(name = "Haber Global", url = "https://haberglobaldvr.blutv.com/blutv_haberglobal_dvr/live.m3u8", id = 10))
    channelList.add(Channel(name = "Habertürk TV", url = "https://ciner-live.daioncdn.net/haberturktv/haberturktv.m3u8", id = 11))
    channelList.add(Channel(name = "Halk TV", url = "https://halktv-live.daioncdn.net/halktv/halktv.m3u8", id = 12))
    channelList.add(Channel(name = "NTV", url = "https://dogus-live.daioncdn.net/ntv/ntv.m3u8", id = 13))
    channelList.add(Channel(name = "Bloomberg HT", url = "https://ciner.daioncdn.net/bloomberght/bloomberght.m3u8", id = 14))
    channelList.add(Channel(name = "CNBC-e", url = "https://hnpsechtsc.turknet.ercdn.net/xpnvudnlsv/cnbc-e/cnbc-e.m3u8", id = 15))
    channelList.add(Channel(name = "Tele 1", url = "https://tele1-live.ercdn.net/tele1/tele1.m3u8", id = 16))
    channelList.add(Channel(name = "TGRT Haber", url = "https://canli.tgrthaber.com/tgrt.m3u8", id = 17))
    channelList.add(Channel(name = "Beyaz TV", url = "https://beyaztv.daioncdn.net/beyaztv/beyaztv.m3u8", id = 18))
    channelList.add(Channel(name = "TRT Spor", url = "https://tv-trtspor1.medya.trt.com.tr/master.m3u8", id = 19))
    channelList.add(Channel(name = "HT Spor TV", url = "https://ciner.daioncdn.net/ht-spor/ht-spor.m3u8?app=web", id = 20))
    channelList.add(Channel(name = "A Spor", url = "https://mn-nl.mncdn.com/blutv_aspor/smil:aspor_sd.smil/playlist.m3u8", id = 21))
    channelList.add(Channel(name = "FB TV", url = "http://1hskrdto.rocketcdn.com/fenerbahcetv.smil/playlist.m3u8", id = 22))
    channelList.add(Channel(name = "TJK TV", url = "https://tjktv-live.tjk.org/tjktv.m3u8", id = 23))
    channelList.add(Channel(name = "TRT Belgesel", url = "https://tv-trtbelgesel.medya.trt.com.tr/master.m3u8", id = 24))
    channelList.add(Channel(name = "TGRT Belgesel", url = "https://tv.ensonhaber.com/tv/tr/tgrtbelgesel/index.m3u8", id = 25))
    channelList.add(Channel(name = "Kral Pop TV", url = "https://dogus-live.daioncdn.net/kralpoptv/playlist.m3u8", id = 26))
    channelList.add(Channel(name = "Power Türk TV", url = "https://livetv.powerapp.com.tr/powerturkTV/powerturkhd.smil/playlist.m3u8", id = 27))
    channelList.add(Channel(name = "360 TV", url = "https://turkmedya-live.ercdn.net/tv360/tv360.m3u8", id = 28))
    channelList.add(Channel(name = "24 TV", url = "https://turkmedya-live.ercdn.net/tv24/tv24.m3u8", id = 29))
    channelList.add(Channel(name = "Flash TV", url = "https://mn-nl.mncdn.com/blutv_flashtv/live.m3u8", id = 30))
    channelList.add(Channel(name = "Ülke TV", url = "https://mn-nl.mncdn.com/blutv_ulketv2/live.m3u8", id = 31))
    channelList.add(Channel(name = "TVnet", url = "https://mn-nl.mncdn.com/tvnet/tvnet/playlist.m3u8", id = 32))
    channelList.add(Channel(name = "TRT Çocuk", url = "https://tv-trtcocuk.medya.trt.com.tr/master.m3u8", id = 33))
    channelList.add(Channel(name = "Minika Çocuk", url = "https://tgn.bozztv.com/dvrfl05/gin-minikacocuk/index.m3u8", id = 34))
    channelList.add(Channel(name = "S Sport", url = "https://bcovlive-a.akamaihd.net/540fcb034b144b848e7ff887f61a293a/eu-central-1/6415845530001/profile_0/chunklist.m3u8", id = 35))
    channelList.add(Channel(name = "4U TV (720p)", url = "https://hls.4utv.live/hls/stream.m3u8", id = 36))
    channelList.add(Channel(name = "24 TV (1080p)", url = "https://mn-nl.mncdn.com/kanal24/smil:kanal24.smil/playlist.m3u8", id = 37))
    channelList.add(Channel(name = "360 TV (720p)", url = "https://turkmedya-live.ercdn.net/tv360/tv360.m3u8", id = 38))
    channelList.add(Channel(name = "A Türk Izmir", url = "https://vdo.digitalbox.xyz:3807/stream/play.m3u8", id = 39))
    channelList.add(Channel(name = "AA Live", url = "https://mtulqxgomrllive.mediatriple.net/mtulqxgomrllive/broadcast_59f9c0c785b88.smil/playlist.m3u8", id = 40))
    channelList.add(Channel(name = "Afroturk TV", url = "https://edge.socialsmart.tv/naturaltv/bant1/playlist.m3u8", id = 41))
    channelList.add(Channel(name = "Akit TV", url = "https://akittv-live.ercdn.net/akittv/akittv.m3u8", id = 42))
    channelList.add(Channel(name = "Aksu TV", url = "https://live.artidijitalmedya.com/artidijital_aksutv/aksutv/playlist.m3u8", id = 43))
    channelList.add(Channel(name = "Al-Zahra TV", url = "https://live.al-zahratv.com/live/playlist.m3u8", id = 44))
    channelList.add(Channel(name = "Alanya Posta TV", url = "https://api-tv3.yayin.com.tr/postatv/postatv/playlist.m3u8", id = 45))
    channelList.add(Channel(name = "Almahriah TV", url = "https://starmenajo.com/hls/almahriah/index.m3u8", id = 46))
    channelList.add(Channel(name = "Altas TV", url = "https://edge1.socialsmart.tv/altastv/bant1/playlist.m3u8", id = 47))
    channelList.add(Channel(name = "Anadolu Net TV", url = "https://live.artidijitalmedya.com/artidijital_anadolunet/anadolunet/playlist.m3u8", id = 48))
    channelList.add(Channel(name = "ARAS TV", url = "http://1.rtmp.org/tv217/yayin.stream/playlist.m3u8", id = 49))
    channelList.add(Channel(name = "ATV (360p)", url = "http://rnttwmjcin.turknet.ercdn.net/lcpmvefbyo/atv/atv_360p.m3u8", id = 50))
    channelList.add(Channel(name = "ATV (Direct)", url = "http://89.187.191.41/ATV-HD-TR/video.m3u8", id = 51))
    channelList.add(Channel(name = "ATV (Alternative)", url = "http://116.202.238.88/ATV_TR/index.m3u8", id = 52))
    channelList.add(Channel(name = "ATV Alanya", url = "http://stream2.taksimbilisim.com:1935/alanyatv/alanyatv.smil/playlist.m3u8", id = 53))
    channelList.add(Channel(name = "ATV Avrupa", url = "https://streamer2.nexgen.bz/ATV/index.m3u8", id = 54))
    channelList.add(Channel(name = "Bengü Türk", url = "https://tv.ensonhaber.com/benguturk/benguturk.m3u8", id = 55))
    channelList.add(Channel(name = "Berat TV", url = "https://cdn-berattv.yayin.com.tr/berattv/berattv/playlist.m3u8", id = 56))
    channelList.add(Channel(name = "Bir TV", url = "https://live.artidijitalmedya.com/artidijital_birtv/birtv/playlist.m3u8", id = 57))
    channelList.add(Channel(name = "Bizimev TV", url = "https://mn-nl.mncdn.com/blutv_bizimev/bizimev_sd.smil/playlist.m3u8", id = 58))
    channelList.add(Channel(name = "Bursa AS TV", url = "https://live.artidijitalmedya.com/artidijital_astv/astv/playlist.m3u8", id = 59))
    channelList.add(Channel(name = "Bursa TV", url = "https://cdn-bursatv.yayin.com.tr/bursatv/bursatv/playlist.m3u8", id = 60))
    channelList.add(Channel(name = "Can TV", url = "https://livetv.canbroadcast.com:7443/canlican/tv.m3u8", id = 61))
    channelList.add(Channel(name = "Cay TV", url = "https://edge1.socialsmart.tv/caytv/bant1/playlist.m3u8", id = 62))
    channelList.add(Channel(name = "Çekmeköy TV", url = "https://cdn-cekmekoybeltv.yayin.com.tr/cekmekoybeltv/cekmekoybeltv/playlist.m3u8", id = 63))
    channelList.add(Channel(name = "CGTN Documentary", url = "https://mn-nl.mncdn.com/dogusdyg_drone/cgtn/playlist.m3u8", id = 64))
    channelList.add(Channel(name = "Çiftçi TV", url = "https://live.artidijitalmedya.com/artidijital_ciftcitv/ciftcitv/playlist.m3u8", id = 65))
    channelList.add(Channel(name = "Deha TV", url = "https://live.artidijitalmedya.com/artidijital_dehatv/dehatv/playlist.m3u8", id = 66))
    channelList.add(Channel(name = "Deniz Postası TV", url = "https://live.artidijitalmedya.com/artidijital_denizpostasi/denizpostasi/playlist.m3u8", id = 67))
    channelList.add(Channel(name = "DHA", url = "https://603c568fccdf5.streamlock.net/live/dhaweb1_C5efC/playlist.m3u8", id = 68))
    channelList.add(Channel(name = "DİM TV", url = "https://live.artidijitalmedya.com/artidijital_dimtv/dimtv/playlist.m3u8", id = 69))
    channelList.add(Channel(name = "Disney Jr", url = "https://saran-live.ercdn.net/disneyjunior/index.m3u8", id = 70))
    channelList.add(Channel(name = "Diyanet TV", url = "https://eustr73.mediatriple.net/videoonlylive/mtikoimxnztxlive/broadcast_5e3bf95a47e07.smil/playlist.m3u8", id = 71))
    channelList.add(Channel(name = "Diyar TV", url = "https://live.artidijitalmedya.com/artidijital_diyartv/diyartv/playlist.m3u8", id = 72))
    channelList.add(Channel(name = "Dost TV", url = "https://dost.stream.emsal.im/tv/live.m3u8", id = 73))
    channelList.add(Channel(name = "Dream Türk", url = "https://live.duhnet.tv/S2/HLS_LIVE/dreamturknp/playlist.m3u8", id = 74))
    channelList.add(Channel(name = "Edessa TV", url = "https://tv.digitalbox.xyz:19360/edessatv/edessatv.m3u8", id = 75))
    channelList.add(Channel(name = "Er TV", url = "https://live.artidijitalmedya.com/artidijital_ertv_new/ertv/playlist.m3u8", id = 76))
    channelList.add(Channel(name = "Erzurum Web TV", url = "https://win29.yayin.com.tr/erzurumwebtv/erzurumwebtv/iptvdelisi.m3u8", id = 77))
    channelList.add(Channel(name = "ES TV", url = "https://live.artidijitalmedya.com/artidijital_estv/estv/playlist.m3u8", id = 78))
    channelList.add(Channel(name = "ETV Kayseri", url = "https://live.artidijitalmedya.com/artidijital_etv/etv/playlist.m3u8", id = 79))
    channelList.add(Channel(name = "ETV Manisa", url = "https://edge1.socialsmart.tv/manisaetv/bant1/playlist.m3u8", id = 80))
    channelList.add(Channel(name = "Euro D", url = "https://live.duhnet.tv/S2/HLS_LIVE/eurodnp/playlist.m3u8", id = 81))
    channelList.add(Channel(name = "EuroStar TV", url = "https://canlitvulusal.xyz/live/eurostar/index.m3u8", id = 82))
    channelList.add(Channel(name = "Finans Türk TV", url = "https://yayin30.haber100.com/live/finansturk/playlist.m3u8", id = 83))
    channelList.add(Channel(name = "Fortuna TV", url = "https://edge1.socialsmart.tv/ftvturk/bant1/playlist.m3u8", id = 84))
    channelList.add(Channel(name = "FX", url = "https://saran-live.ercdn.net/fx/index.m3u8", id = 85))
    channelList.add(Channel(name = "GRT", url = "https://live.artidijitalmedya.com/artidijital_grt/grt1/playlist.m3u8", id = 86))
    channelList.add(Channel(name = "Güneydoğu TV", url = "https://edge1.socialsmart.tv/gtv/bant1/playlist.m3u8", id = 87))
    channelList.add(Channel(name = "GZT", url = "https://mn-nl.mncdn.com/gzttv/gzttv/playlist.m3u8", id = 88))
    channelList.add(Channel(name = "Haber 61 TV", url = "https://win8.yayin.com.tr/haber61tv/smil:haber61tv.smil/playlist.m3u8", id = 89))
    channelList.add(Channel(name = "Helwa TV", url = "https://raw.githubusercontent.com/ipstreet312/freeiptv/master/ressources/dzflix/helwa.m3u8", id = 90))
    channelList.add(Channel(name = "Hunat TV", url = "https://live.artidijitalmedya.com/artidijital_hunattv/hunattv/playlist.m3u8", id = 91))
    channelList.add(Channel(name = "IBB TV", url = "https://npserver1.ibb.gov.tr/webtv/webtv_wowza1/playlist.m3u8", id = 92))
    channelList.add(Channel(name = "İçel TV", url = "https://edge1.socialsmart.tv/iceltv/bant1/playlist.m3u8", id = 93))
    channelList.add(Channel(name = "Kanal 3", url = "https://live.artidijitalmedya.com/artidijital_kanal3/kanal3/playlist.m3u8", id = 94))
    channelList.add(Channel(name = "Kanal 7 Avrupa", url = "https://livetv.radyotvonline.net/kanal7live/kanal7avr/playlist.m3u8", id = 95))
    channelList.add(Channel(name = "Kanal 12", url = "https://live.artidijitalmedya.com/artidijital_kanal12/kanal12/playlist.m3u8", id = 96))
    channelList.add(Channel(name = "Kanal 15", url = "https://live.artidijitalmedya.com/artidijital_kanal15/kanal15/playlist.m3u8", id = 97))
    channelList.add(Channel(name = "Kanal 23", url = "https://live.artidijitalmedya.com/artidijital_kanal23/kanal23/playlist.m3u8", id = 98))
    channelList.add(Channel(name = "Kanal 26", url = "https://live.artidijitalmedya.com/artidijital_kanal26/kanal26/playlist.m3u8", id = 99))
    channelList.add(Channel(name = "Kanal 32", url = "https://edge1.socialsmart.tv/kanal32/bant1/playlist.m3u8", id = 100))
    channelList.add(Channel(name = "Kanal 33", url = "https://edge1.socialsmart.tv/kanal33/bant1/playlist.m3u8", id = 101))
    channelList.add(Channel(name = "Kanal 34", url = "https://5be5d840359c6.streamlock.net/kanal34tv/kanal34tv/playlist.m3u8", id = 102))
    channelList.add(Channel(name = "Kanal 38", url = "http://yayin3.canlitv.com:1935/live/kanal38/playlist.m3u8", id = 103))
    channelList.add(Channel(name = "Kanal 58", url = "https://edge1.socialsmart.tv/kanal58/bant1/playlist.m3u8", id = 104))
    channelList.add(Channel(name = "Kanal 68", url = "https://live.artidijitalmedya.com/artidijital_kanal68/kanal68/playlist.m3u8", id = 105))
    channelList.add(Channel(name = "Kanal Avrupa", url = "http://51.15.2.151/hls/kanalavrupa.m3u8", id = 106))
    channelList.add(Channel(name = "Kanal B", url = "http://212.174.58.161/hls-live/livepkgr/_definst_/liveevent/kanalb.m3u8", id = 107))
    channelList.add(Channel(name = "Kanal D Drama", url = "https://cdn-uw2-prod.tsv2.amagi.tv/linear/amg01602-themahqfrance-vivekanald-samsungspain/playlist.m3u8", id = 108))
    channelList.add(Channel(name = "Kanal Fırat", url = "https://live.artidijitalmedya.com/artidijital_kanalfirat/kanalfirat/playlist.m3u8", id = 109))
    channelList.add(Channel(name = "Kanal Hayat", url = "https://tbn02a.ltnschedule.com/hls/nx21i.m3u8", id = 110))
    channelList.add(Channel(name = "Kanal V", url = "https://live.artidijitalmedya.com/artidijital_kanalv/kanalv/playlist.m3u8", id = 111))
    channelList.add(Channel(name = "Kanal Z", url = "https://live.artidijitalmedya.com/artidijital_kanalz/kanalz/playlist.m3u8", id = 112))
    channelList.add(Channel(name = "Kay TV", url = "https://live.artidijitalmedya.com/artidijital_kaytv/kaytv1/playlist.m3u8", id = 113))
    channelList.add(Channel(name = "Kent Türk TV", url = "https://live.artidijitalmedya.com/artidijital_kentturktv/kentturktv/playlist.m3u8", id = 114))
    channelList.add(Channel(name = "Konya Olay TV", url = "https://live.artidijitalmedya.com/artidijital_konyaolaytv/konyaolaytv/playlist.m3u8", id = 115))
    channelList.add(Channel(name = "Kudüs TV", url = "https://yayin.kudustv.com/981680400/kudustv/playlist.m3u8", id = 116))
    channelList.add(Channel(name = "Lalegül TV", url = "https://lbl.netmedya.net/hls/lalegultv.m3u8", id = 117))
    channelList.add(Channel(name = "Life TV", url = "https://live.artidijitalmedya.com/artidijital_lifetv/lifetv/playlist.m3u8", id = 118))
    channelList.add(Channel(name = "Line TV", url = "https://edge1.socialsmart.tv/linetv/bant1/playlist.m3u8", id = 119))
    channelList.add(Channel(name = "Mavi Karadeniz", url = "https://live.artidijitalmedya.com/artidijital_mavikaradeniz/mavikaradeniz/playlist.m3u8", id = 120))
    channelList.add(Channel(name = "Med Müzik", url = "http://54.36.110.140/live3/live3.m3u8", id = 121))
    channelList.add(Channel(name = "Mekameleen TV", url = "https://mn-nl.mncdn.com/mekameleen/smil:mekameleentv.smil/playlist.m3u8", id = 122))
    channelList.add(Channel(name = "Meltem TV", url = "https://vhxyrsly.rocketcdn.com/meltemtv/playlist.m3u8", id = 123))
    channelList.add(Channel(name = "Mercan TV", url = "https://live.artidijitalmedya.com/artidijital_mercantv/mercantv/playlist.m3u8", id = 124))
    channelList.add(Channel(name = "Milyon TV", url = "https://sosyoapp-live.cdnnew.com/sosyo/buraya-bir-isim-verin.m3u8", id = 125))
    channelList.add(Channel(name = "Minika Go", url = "https://tgn.bozztv.com/dvrfl05/gin-minikago/index.m3u8", id = 126))
    channelList.add(Channel(name = "MovieSmart Türk", url = "http://playhdnewjj.xyz:8080/recc121412/KVqfhtdJ2nQ7/174", id = 127))
    channelList.add(Channel(name = "MTürk TV", url = "https://edge1.socialsmart.tv/muglaturk/bant1/playlist.m3u8", id = 128))
    channelList.add(Channel(name = "National Geographic", url = "https://saran-live.ercdn.net/natgeohd/index.m3u8", id = 129))
    channelList.add(Channel(name = "Nat Geo Wild", url = "https://saran-live.ercdn.net/natgeowild/index.m3u8", id = 130))
    channelList.add(Channel(name = "Natural TV", url = "https://edge1.socialsmart.tv/naturaltv/bant1/playlist.m3u8", id = 131))
    channelList.add(Channel(name = "Number 1 Ask", url = "https://b01c02nl.mediatriple.net/videoonlylive/mtkgeuihrlfwlive/u_stream_5c9e18f9cea15_1/playlist.m3u8", id = 132))
    channelList.add(Channel(name = "Number 1 Damar", url = "https://b01c02nl.mediatriple.net/videoonlylive/mtkgeuihrlfwlive/u_stream_5c9e198784bdc_1/playlist.m3u8", id = 133))
    channelList.add(Channel(name = "Number 1 Dance", url = "https://b01c02nl.mediatriple.net/videoonlylive/mtkgeuihrlfwlive/u_stream_5c9e2aa8acf44_1/playlist.m3u8", id = 134))
    channelList.add(Channel(name = "Number 1 Türk", url = "https://mn-nl.mncdn.com/blutv_nr1turk2/live.m3u8", id = 135))
    channelList.add(Channel(name = "Number 1 TV", url = "https://mn-nl.mncdn.com/blutv_nr12/live.m3u8", id = 136))
    channelList.add(Channel(name = "Olay Türk TV", url = "https://live.artidijitalmedya.com/artidijital_olayturk/olayturk/playlist.m3u8", id = 137))
    channelList.add(Channel(name = "On 4", url = "https://edge1.socialsmart.tv/on4/bant1/playlist.m3u8", id = 138))
    channelList.add(Channel(name = "On 6", url = "https://live.artidijitalmedya.com/artidijital_kanal16/kanal16/playlist.m3u8", id = 139))
    channelList.add(Channel(name = "ON Medya Haber", url = "http://live.arkumedia.com:1935/marmaratv/marmaratv/playlist.m3u8", id = 140))
    channelList.add(Channel(name = "Öncü TV", url = "https://edge1.socialsmart.tv/oncurtv/bant1/playlist.m3u8", id = 141))
    channelList.add(Channel(name = "Power Dance", url = "https://livetv.powerapp.com.tr/dance/dance.smil/playlist.m3u8", id = 142))
    channelList.add(Channel(name = "Power Love", url = "https://livetv.powerapp.com.tr/plove/love.smil/playlist.m3u8", id = 143))
    channelList.add(Channel(name = "Power Türk Akustik", url = "https://livetv.powerapp.com.tr/pturkakustik/akustik.smil/playlist.m3u8", id = 144))
    channelList.add(Channel(name = "Power Türk Slow", url = "https://livetv.powerapp.com.tr/pturkslow/slow.smil/playlist.m3u8", id = 145))
    channelList.add(Channel(name = "Power TV", url = "https://livetv.powerapp.com.tr/powerTV/powerhd.smil/playlist.m3u8", id = 146))
    channelList.add(Channel(name = "S Sport 2", url = "https://bcovlive-a.akamaihd.net/29c60f23ea4840ba8726925a77fcfd0b/eu-central-1/6415845530001/profile_0/chunklist.m3u8", id = 147))
    channelList.add(Channel(name = "Sat7 Türk", url = "https://svs.itworkscdn.net/sat7turklive/sat7turk.smil/playlist.m3u8", id = 148))
    channelList.add(Channel(name = "Satranç TV", url = "http://139.162.182.79/live/test/index.m3u8", id = 149))
    channelList.add(Channel(name = "Semerkand TV", url = "https://b01c02nl.mediatriple.net/videoonlylive/mtisvwurbfcyslive/broadcast_58d915bd40efc.smil/playlist.m3u8", id = 150))
    channelList.add(Channel(name = "Sports TV", url = "https://live.sportstv.com.tr/hls/low/sportstv.m3u8", id = 151))
    channelList.add(Channel(name = "Sun RTV", url = "https://live.artidijitalmedya.com/artidijital_sunrtv/sunrtv/playlist.m3u8", id = 152))
    channelList.add(Channel(name = "TRT 1", url = "https://trt.daioncdn.net/trt-1/master.m3u8?app=web", id = 1))
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            val digit = keyCode - KeyEvent.KEYCODE_0
            inputBuffer.append(digit)
            
            handler.removeCallbacks(jumpRunnable)
            handler.postDelayed(jumpRunnable, 1200)
            
            Toast.makeText(this, "Kanal: $inputBuffer", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun processChannelJump() {
        val targetNo = inputBuffer.toString().toIntOrNull()
        inputBuffer.setLength(0)

        if (targetNo != null) {
            val index = channelList.indexOfFirst { it.id == targetNo }
            if (index != -1) {
                recyclerView.scrollToPosition(index)
                recyclerView.postDelayed({
                    val vh = recyclerView.findViewHolderForAdapterPosition(index)
                    vh?.itemView?.requestFocus()
                    playInPreview(channelList[index].url)
                }, 200)
            } else {
                Toast.makeText(this, "Kanal $targetNo bulunamadı", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDragAndDrop() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val from = vh.adapterPosition
                val to = target.adapterPosition
                Collections.swap(channelList, from, to)
                reassignChannelIds()
                adapter.notifyItemMoved(from, to)
                adapter.notifyItemRangeChanged(0, channelList.size)
                return true
            }
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun reassignChannelIds() {
        for (i in 0 until channelList.size) {
            channelList[i].id = i + 1
        }
    }

    private fun playInPreview(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        previewPlayer?.setMediaItem(mediaItem)
        previewPlayer?.prepare()
        previewPlayer?.play()
    }

    private fun handleInteraction(channel: Channel) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < doubleClickTimeout) {
            openFullScreen(channel.url)
        } else {
            playInPreview(channel.url)
        }
        lastClickTime = currentTime
    }

    private fun openFullScreen(url: String) {
        previewPlayer?.pause()
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }

    private fun showOptionsDialog(channel: Channel, position: Int) {
        val options = arrayOf("Düzenle", "Sil")
        AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle(channel.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(channel, position)
                    1 -> deleteChannel(position)
                }
            }.show()
    }

    private fun deleteChannel(position: Int) {
        channelList.removeAt(position)
        reassignChannelIds() // Numaraları 1, 2, 3 diye yeniden düzenle
        adapter.notifyDataSetChanged()
        Toast.makeText(this, "Kanal silindi", Toast.LENGTH_SHORT).show()
    }

    private fun showEditDialog(channel: Channel, position: Int) {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_channel, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etUrl = dialogView.findViewById<EditText>(R.id.etUrl)
        etName.setText(channel.name)
        etUrl.setText(channel.url)

        builder.setView(dialogView).setTitle("Düzenle")
            .setPositiveButton("GÜNCELLE") { _, _ ->
                channel.name = etName.text.toString()
                channel.url = etUrl.text.toString()
                adapter.notifyItemChanged(position)
            }.show()
    }

    private fun showAddDialog() {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_channel, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etUrl = dialogView.findViewById<EditText>(R.id.etUrl)

        builder.setView(dialogView).setTitle("Yeni Kanal")
            .setPositiveButton("EKLE") { _, _ ->
                val name = etName.text.toString().trim()
                val url = etUrl.text.toString().trim()
                if (name.isNotEmpty() && url.isNotEmpty()) {
                    channelList.add(Channel(name = name, url = url, id = channelList.size + 1))
                    adapter.notifyItemInserted(channelList.size - 1)
                }
            }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        previewPlayer?.release()
    }
}
