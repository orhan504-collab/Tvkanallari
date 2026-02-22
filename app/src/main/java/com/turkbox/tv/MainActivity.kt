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
    
    // Kanal Numarası Tuşlama Ayarları
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
        
        // Kanalları yükle ve numaralandır
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
                // i + 1 yaparak 1, 2, 3... diye numara veriyoruz
                channelList.add(Channel(i + 1, obj.getString("name"), obj.getString("url")))
            }
        } catch (e: Exception) {
            if (channelList.isEmpty()) {
                channelList.add(Channel(1, "TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))
                channelList.add(Channel("Show TV", "https://mn-nl.mncdn.com/blutv_showtv/smil:show_sd.smil/playlist.m3u8"))
channelList.add(Channel("TRT 1", "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"))
channelList.add(Channel("Kanal D", "https://demiroren.daioncdn.net/kanald/kanald.m3u8?app=kanald_web"))
channelList.add(Channel("ATV", "https://trkvz-live.daioncdn.net/atv/atv.m3u8"))
channelList.add(Channel("NOW TV", "https://uycyyuuzyh.turknet.ercdn.net/nphindgytw/nowtv/nowtv.m3u8"))
channelList.add(Channel("Star TV", "https://mn-nl.mncdn.com/blutv_star/smil:star_sd.smil/playlist.m3u8"))
channelList.add(Channel("TV 8", "https://tv8.daioncdn.net/tv8/tv8.m3u8"))
channelList.add(Channel("Kanal 7", "https://kanal7-live.daioncdn.net/kanal7/kanal7.m3u8"))
channelList.add(Channel("TV 100", "https://tv100-live.daioncdn.net/tv100/tv100.m3u8"))
channelList.add(Channel("Haber Global", "https://haberglobaldvr.blutv.com/blutv_haberglobal_dvr/live.m3u8"))
channelList.add(Channel("Habertürk TV", "https://ciner-live.daioncdn.net/haberturktv/haberturktv.m3u8"))
channelList.add(Channel("Halk TV", "https://halktv-live.daioncdn.net/halktv/halktv.m3u8"))
channelList.add(Channel("NTV", "https://dogus-live.daioncdn.net/ntv/ntv.m3u8"))
channelList.add(Channel("Bloomberg HT", "https://ciner.daioncdn.net/bloomberght/bloomberght.m3u8"))
channelList.add(Channel("CNBC-e", "https://hnpsechtsc.turknet.ercdn.net/xpnvudnlsv/cnbc-e/cnbc-e.m3u8"))
channelList.add(Channel("Tele 1", "https://tele1-live.ercdn.net/tele1/tele1.m3u8"))
channelList.add(Channel("TGRT Haber", "https://canli.tgrthaber.com/tgrt.m3u8"))
channelList.add(Channel("Beyaz TV", "https://beyaztv.daioncdn.net/beyaztv/beyaztv.m3u8"))
channelList.add(Channel("TRT Spor", "https://tv-trtspor1.medya.trt.com.tr/master.m3u8"))
channelList.add(Channel("HT Spor TV", "https://ciner.daioncdn.net/ht-spor/ht-spor.m3u8?app=web"))
channelList.add(Channel("A Spor", "https://mn-nl.mncdn.com/blutv_aspor/smil:aspor_sd.smil/playlist.m3u8"))
channelList.add(Channel("FB TV", "http://1hskrdto.rocketcdn.com/fenerbahcetv.smil/playlist.m3u8"))
channelList.add(Channel("TJK TV", "https://tjktv-live.tjk.org/tjktv.m3u8"))
channelList.add(Channel("TRT Belgesel", "https://tv-trtbelgesel.medya.trt.com.tr/master.m3u8"))
channelList.add(Channel("TGRT Belgesel", "https://tv.ensonhaber.com/tv/tr/tgrtbelgesel/index.m3u8"))
channelList.add(Channel("Kral Pop TV", "https://dogus-live.daioncdn.net/kralpoptv/playlist.m3u8"))
channelList.add(Channel("Power Türk TV", "https://livetv.powerapp.com.tr/powerturkTV/powerturkhd.smil/playlist.m3u8"))
channelList.add(Channel("360 TV", "https://turkmedya-live.ercdn.net/tv360/tv360.m3u8"))
channelList.add(Channel("24 TV", "https://turkmedya-live.ercdn.net/tv24/tv24.m3u8"))
channelList.add(Channel("Flash TV", "https://mn-nl.mncdn.com/blutv_flashtv/live.m3u8"))
channelList.add(Channel("Ülke TV", "https://mn-nl.mncdn.com/blutv_ulketv2/live.m3u8"))
channelList.add(Channel("TVnet", "https://mn-nl.mncdn.com/tvnet/tvnet/playlist.m3u8"))
channelList.add(Channel("TRT Çocuk", "https://tv-trtcocuk.medya.trt.com.tr/master.m3u8"))
channelList.add(Channel("Minika Çocuk", "https://tgn.bozztv.com/dvrfl05/gin-minikacocuk/index.m3u8"))
channelList.add(Channel("S Sport", "https://bcovlive-a.akamaihd.net/540fcb034b144b848e7ff887f61a293a/eu-central-1/6415845530001/profile_0/chunklist.m3u8"))
channelList.add(Channel("4U TV (720p)", "https://hls.4utv.live/hls/stream.m3u8"))
channelList.add(Channel("24 TV (1080p)", "https://mn-nl.mncdn.com/kanal24/smil:kanal24.smil/playlist.m3u8"))
channelList.add(Channel("A Türk Izmir", "https://vdo.digitalbox.xyz:3807/stream/play.m3u8"))
channelList.add(Channel("AA Live", "https://mtulqxgomrllive.mediatriple.net/mtulqxgomrllive/broadcast_59f9c0c785b88.smil/playlist.m3u8"))
channelList.add(Channel("Afroturk TV", "https://edge.socialsmart.tv/naturaltv/bant1/playlist.m3u8"))
channelList.add(Channel("Akit TV", "https://akittv-live.ercdn.net/akittv/akittv.m3u8"))
channelList.add(Channel("Aksu TV", "https://live.artidijitalmedya.com/artidijital_aksutv/aksutv/playlist.m3u8"))
channelList.add(Channel("Al-Zahra TV", "https://live.al-zahratv.com/live/playlist.m3u8"))
channelList.add(Channel("Alanya Posta TV", "https://api-tv3.yayin.com.tr/postatv/postatv/playlist.m3u8"))
channelList.add(Channel("Almahriah TV", "https://starmenajo.com/hls/almahriah/index.m3u8"))
channelList.add(Channel("Altas TV", "https://edge1.socialsmart.tv/altastv/bant1/playlist.m3u8"))
channelList.add(Channel("Anadolu Net TV", "https://live.artidijitalmedya.com/artidijital_anadolunet/anadolunet/playlist.m3u8"))
channelList.add(Channel("ARAS TV", "http://1.rtmp.org/tv217/yayin.stream/playlist.m3u8"))
channelList.add(Channel("ATV (360p)", "http://rnttwmjcin.turknet.ercdn.net/lcpmvefbyo/atv/atv_360p.m3u8"))
channelList.add(Channel("ATV (Direct)", "http://89.187.191.41/ATV-HD-TR/video.m3u8"))
channelList.add(Channel("ATV (Alternative)", "http://116.202.238.88/ATV_TR/index.m3u8"))
channelList.add(Channel("ATV Alanya", "http://stream2.taksimbilisim.com:1935/alanyatv/alanyatv.smil/playlist.m3u8"))
channelList.add(Channel("ATV Avrupa", "https://streamer2.nexgen.bz/ATV/index.m3u8"))
channelList.add(Channel("Bengü Türk", "https://tv.ensonhaber.com/benguturk/benguturk.m3u8"))
channelList.add(Channel("Berat TV", "https://cdn-berattv.yayin.com.tr/berattv/berattv/playlist.m3u8"))
channelList.add(Channel("Bir TV", "https://live.artidijitalmedya.com/artidijital_birtv/birtv/playlist.m3u8"))
channelList.add(Channel("Bizimev TV", "https://mn-nl.mncdn.com/blutv_bizimev/bizimev_sd.smil/playlist.m3u8"))
channelList.add(Channel("Bursa AS TV", "https://live.artidijitalmedya.com/artidijital_astv/astv/playlist.m3u8"))
channelList.add(Channel("Bursa TV", "https://cdn-bursatv.yayin.com.tr/bursatv/bursatv/playlist.m3u8"))
channelList.add(Channel("Can TV", "https://livetv.canbroadcast.com:7443/canlican/tv.m3u8"))
channelList.add(Channel("Cay TV", "https://edge1.socialsmart.tv/caytv/bant1/playlist.m3u8"))
channelList.add(Channel("Çekmeköy TV", "https://cdn-cekmekoybeltv.yayin.com.tr/cekmekoybeltv/cekmekoybeltv/playlist.m3u8"))
channelList.add(Channel("CGTN Documentary", "https://mn-nl.mncdn.com/dogusdyg_drone/cgtn/playlist.m3u8"))
channelList.add(Channel("Çiftçi TV", "https://live.artidijitalmedya.com/artidijital_ciftcitv/ciftcitv/playlist.m3u8"))
channelList.add(Channel("Deha TV", "https://live.artidijitalmedya.com/artidijital_dehatv/dehatv/playlist.m3u8"))
channelList.add(Channel("Deniz Postası TV", "https://live.artidijitalmedya.com/artidijital_denizpostasi/denizpostasi/playlist.m3u8"))
channelList.add(Channel("DHA", "https://603c568fccdf5.streamlock.net/live/dhaweb1_C5efC/playlist.m3u8"))
channelList.add(Channel("DİM TV", "https://live.artidijitalmedya.com/artidijital_dimtv/dimtv/playlist.m3u8"))
channelList.add(Channel("Disney Jr", "https://saran-live.ercdn.net/disneyjunior/index.m3u8"))
channelList.add(Channel("Diyanet TV", "https://eustr73.mediatriple.net/videoonlylive/mtikoimxnztxlive/broadcast_5e3bf95a47e07.smil/playlist.m3u8"))
channelList.add(Channel("Diyar TV", "https://live.artidijitalmedya.com/artidijital_diyartv/diyartv/playlist.m3u8"))
channelList.add(Channel("Dost TV", "https://dost.stream.emsal.im/tv/live.m3u8"))
channelList.add(Channel("Dream Türk", "https://live.duhnet.tv/S2/HLS_LIVE/dreamturknp/playlist.m3u8"))
channelList.add(Channel("Edessa TV", "https://tv.digitalbox.xyz:19360/edessatv/edessatv.m3u8"))
channelList.add(Channel("Er TV", "https://live.artidijitalmedya.com/artidijital_ertv_new/ertv/playlist.m3u8"))
channelList.add(Channel("Erzurum Web TV", "https://win29.yayin.com.tr/erzurumwebtv/erzurumwebtv/iptvdelisi.m3u8"))
channelList.add(Channel("ES TV", "https://live.artidijitalmedya.com/artidijital_estv/estv/playlist.m3u8"))
channelList.add(Channel("ETV Kayseri", "https://live.artidijitalmedya.com/artidijital_etv/etv/playlist.m3u8"))
channelList.add(Channel("ETV Manisa", "https://edge1.socialsmart.tv/manisaetv/bant1/playlist.m3u8"))
channelList.add(Channel("Euro D", "https://live.duhnet.tv/S2/HLS_LIVE/eurodnp/playlist.m3u8"))
channelList.add(Channel("EuroStar TV", "https://canlitvulusal.xyz/live/eurostar/index.m3u8"))
channelList.add(Channel("Finans Türk TV", "https://yayin30.haber100.com/live/finansturk/playlist.m3u8"))
channelList.add(Channel("Fortuna TV", "https://edge1.socialsmart.tv/ftvturk/bant1/playlist.m3u8"))
channelList.add(Channel("FX", "https://saran-live.ercdn.net/fx/index.m3u8"))
channelList.add(Channel("GRT", "https://live.artidijitalmedya.com/artidijital_grt/grt1/playlist.m3u8"))
channelList.add(Channel("Güneydoğu TV", "https://edge1.socialsmart.tv/gtv/bant1/playlist.m3u8"))
channelList.add(Channel("GZT", "https://mn-nl.mncdn.com/gzttv/gzttv/playlist.m3u8"))
channelList.add(Channel("Haber 61 TV", "https://win8.yayin.com.tr/haber61tv/smil:haber61tv.smil/playlist.m3u8"))
channelList.add(Channel("Helwa TV", "https://raw.githubusercontent.com/ipstreet312/freeiptv/master/ressources/dzflix/helwa.m3u8"))
channelList.add(Channel("Hunat TV", "https://live.artidijitalmedya.com/artidijital_hunattv/hunattv/playlist.m3u8"))
channelList.add(Channel("IBB TV", "https://npserver1.ibb.gov.tr/webtv/webtv_wowza1/playlist.m3u8"))
channelList.add(Channel("İçel TV", "https://edge1.socialsmart.tv/iceltv/bant1/playlist.m3u8"))
channelList.add(Channel("Kanal 3", "https://live.artidijitalmedya.com/artidijital_kanal3/kanal3/playlist.m3u8"))
channelList.add(Channel("Kanal 7 Avrupa", "https://livetv.radyotvonline.net/kanal7live/kanal7avr/playlist.m3u8"))
channelList.add(Channel("Kanal 12", "https://live.artidijitalmedya.com/artidijital_kanal12/kanal12/playlist.m3u8"))
channelList.add(Channel("Kanal 15", "https://live.artidijitalmedya.com/artidijital_kanal15/kanal15/playlist.m3u8"))
channelList.add(Channel("Kanal 23", "https://live.artidijitalmedya.com/artidijital_kanal23/kanal23/playlist.m3u8"))
channelList.add(Channel("Kanal 26", "https://live.artidijitalmedya.com/artidijital_kanal26/kanal26/playlist.m3u8"))
channelList.add(Channel("Kanal 32", "https://edge1.socialsmart.tv/kanal32/bant1/playlist.m3u8"))
channelList.add(Channel("Kanal 33", "https://edge1.socialsmart.tv/kanal33/bant1/playlist.m3u8"))
channelList.add(Channel("Kanal 34", "https://5be5d840359c6.streamlock.net/kanal34tv/kanal34tv/playlist.m3u8"))
channelList.add(Channel("Kanal 38", "http://yayin3.canlitv.com:1935/live/kanal38/playlist.m3u8"))
channelList.add(Channel("Kanal 58", "https://edge1.socialsmart.tv/kanal58/bant1/playlist.m3u8"))
channelList.add(Channel("Kanal 68", "https://live.artidijitalmedya.com/artidijital_kanal68/kanal68/playlist.m3u8"))
channelList.add(Channel("Kanal Avrupa", "http://51.15.2.151/hls/kanalavrupa.m3u8"))
channelList.add(Channel("Kanal B", "http://212.174.58.161/hls-live/livepkgr/_definst_/liveevent/kanalb.m3u8"))
channelList.add(Channel("Kanal D Drama", "https://cdn-uw2-prod.tsv2.amagi.tv/linear/amg01602-themahqfrance-vivekanald-samsungspain/playlist.m3u8"))
channelList.add(Channel("Kanal Fırat", "https://live.artidijitalmedya.com/artidijital_kanalfirat/kanalfirat/playlist.m3u8"))
channelList.add(Channel("Kanal Hayat", "https://tbn02a.ltnschedule.com/hls/nx21i.m3u8"))
channelList.add(Channel("Kanal V", "https://live.artidijitalmedya.com/artidijital_kanalv/kanalv/playlist.m3u8"))
channelList.add(Channel("Kanal Z", "https://live.artidijitalmedya.com/artidijital_kanalz/kanalz/playlist.m3u8"))
channelList.add(Channel("Kay TV", "https://live.artidijitalmedya.com/artidijital_kaytv/kaytv1/playlist.m3u8"))
channelList.add(Channel("Kent Türk TV", "https://live.artidijitalmedya.com/artidijital_kentturktv/kentturktv/playlist.m3u8"))
channelList.add(Channel("Konya Olay TV", "https://live.artidijitalmedya.com/artidijital_konyaolaytv/konyaolaytv/playlist.m3u8"))
channelList.add(Channel("Kudüs TV", "https://yayin.kudustv.com/981680400/kudustv/playlist.m3u8"))
channelList.add(Channel("Lalegül TV", "https://lbl.netmedya.net/hls/lalegultv.m3u8"))
channelList.add(Channel("Life TV", "https://live.artidijitalmedya.com/artidijital_lifetv/lifetv/playlist.m3u8"))
channelList.add(Channel("Line TV", "https://edge1.socialsmart.tv/linetv/bant1/playlist.m3u8"))
channelList.add(Channel("Mavi Karadeniz", "https://live.artidijitalmedya.com/artidijital_mavikaradeniz/mavikaradeniz/playlist.m3u8"))
channelList.add(Channel("Med Müzik", "http://54.36.110.140/live3/live3.m3u8"))
channelList.add(Channel("Mekameleen TV", "https://mn-nl.mncdn.com/mekameleen/smil:mekameleentv.smil/playlist.m3u8"))
channelList.add(Channel("Meltem TV", "https://vhxyrsly.rocketcdn.com/meltemtv/playlist.m3u8"))
channelList.add(Channel("Mercan TV", "https://live.artidijitalmedya.com/artidijital_mercantv/mercantv/playlist.m3u8"))
channelList.add(Channel("Milyon TV", "https://sosyoapp-live.cdnnew.com/sosyo/buraya-bir-isim-verin.m3u8"))
channelList.add(Channel("Minika Go", "https://tgn.bozztv.com/dvrfl05/gin-minikago/index.m3u8"))
channelList.add(Channel("MovieSmart Türk", "http://playhdnewjj.xyz:8080/recc121412/KVqfhtdJ2nQ7/174"))
channelList.add(Channel("MTürk TV", "https://edge1.socialsmart.tv/muglaturk/bant1/playlist.m3u8"))
channelList.add(Channel("National Geographic", "https://saran-live.ercdn.net/natgeohd/index.m3u8"))
channelList.add(Channel("Nat Geo Wild", "https://saran-live.ercdn.net/natgeowild/index.m3u8"))
channelList.add(Channel("Natural TV", "https://edge1.socialsmart.tv/naturaltv/bant1/playlist.m3u8"))
channelList.add(Channel("Number 1 Ask", "https://b01c02nl.mediatriple.net/videoonlylive/mtkgeuihrlfwlive/u_stream_5c9e18f9cea15_1/playlist.m3u8"))
channelList.add(Channel("Number 1 Damar", "https://b01c02nl.mediatriple.net/videoonlylive/mtkgeuihrlfwlive/u_stream_5c9e198784bdc_1/playlist.m3u8"))
channelList.add(Channel("Number 1 Dance", "https://b01c02nl.mediatriple.net/videoonlylive/mtkgeuihrlfwlive/u_stream_5c9e2aa8acf44_1/playlist.m3u8"))
channelList.add(Channel("Number 1 Türk", "https://mn-nl.mncdn.com/blutv_nr1turk2/live.m3u8"))
channelList.add(Channel("Number 1 TV", "https://mn-nl.mncdn.com/blutv_nr12/live.m3u8"))
channelList.add(Channel("Olay Türk TV", "https://live.artidijitalmedya.com/artidijital_olayturk/olayturk/playlist.m3u8"))
channelList.add(Channel("On 4", "https://edge1.socialsmart.tv/on4/bant1/playlist.m3u8"))
channelList.add(Channel("On 6", "https://live.artidijitalmedya.com/artidijital_kanal16/kanal16/playlist.m3u8"))
channelList.add(Channel("ON Medya Haber", "http://live.arkumedia.com:1935/marmaratv/marmaratv/playlist.m3u8"))
channelList.add(Channel("Öncü TV", "https://edge1.socialsmart.tv/oncurtv/bant1/playlist.m3u8"))
channelList.add(Channel("Power Dance", "https://livetv.powerapp.com.tr/dance/dance.smil/playlist.m3u8"))
channelList.add(Channel("Power Love", "https://livetv.powerapp.com.tr/plove/love.smil/playlist.m3u8"))
channelList.add(Channel("Power Türk Akustik", "https://livetv.powerapp.com.tr/pturkakustik/akustik.smil/playlist.m3u8"))
channelList.add(Channel("Power Türk Slow", "https://livetv.powerapp.com.tr/pturkslow/slow.smil/playlist.m3u8"))
channelList.add(Channel("Power TV", "https://livetv.powerapp.com.tr/powerTV/powerhd.smil/playlist.m3u8"))
channelList.add(Channel("S Sport 2", "https://bcovlive-a.akamaihd.net/29c60f23ea4840ba8726925a77fcfd0b/eu-central-1/6415845530001/profile_0/chunklist.m3u8"))
channelList.add(Channel("Sat7 Türk", "https://svs.itworkscdn.net/sat7turklive/sat7turk.smil/playlist.m3u8"))
channelList.add(Channel("Satranç TV", "http://139.162.182.79/live/test/index.m3u8"))
channelList.add(Channel("Semerkand TV", "https://b01c02nl.mediatriple.net/videoonlylive/mtisvwurbfcyslive/broadcast_58d915bd40efc.smil/playlist.m3u8"))
channelList.add(Channel("Sports TV", "https://live.sportstv.com.tr/hls/low/sportstv.m3u8"))
channelList.add(Channel("Sun RTV", "https://live.artidijitalmedya.com/artidijital_sunrtv/sunrtv/playlist.m3u8"))
channelList.add(Channel("Tarih TV", "https://tv1.arectv30.sbs/live/tarihtv.m3u8"))
channelList.add(Channel("Tarım TV", "https://content.tvkur.com/l/c7e1da7mm25p552d9u9g/master.m3u8"))
channelList.add(Channel("Tatlıses TV", "https://live.artidijitalmedya.com/artidijital_tatlisestv/tatlisestv/playlist.m3u8"))
channelList.add(Channel("TBMM TV", "https://meclistv-live.ercdn.net/meclistv/meclistv.m3u8"))
channelList.add(Channel("Tempo TV", "https://live.artidijitalmedya.com/artidijital_tempotv/tempotv/playlist.m3u8"))
channelList.add(Channel("Teve2", "https://demiroren-live.daioncdn.net/teve2/teve2.m3u8"))
channelList.add(Channel("TGRT Europe", "https://tv.ensonhaber.com/tv/tr/tgrteu/index.m3u8"))
channelList.add(Channel("Tivi 6", "https://live.artidijitalmedya.com/artidijital_tivi6/tivi6/playlist.m3u8"))
channelList.add(Channel("TMB", "https://raw.githubusercontent.com/UzunMuhalefet/streams/main/myvideo-az/tmb-tv.m3u8"))
channelList.add(Channel("Ton TV", "https://live.artidijitalmedya.com/artidijital_tontv/tontv/playlist.m3u8"))
channelList.add(Channel("Toprak TV", "https://59cba4d34b678.streamlock.net/canlitv/topraktv/playlist.m3u8"))
channelList.add(Channel("Torba TV", "https://59cba4d34b678.streamlock.net/canlitv/torbatv/playlist.m3u8"))
channelList.add(Channel("Trakya Türk TV", "https://live.artidijitalmedya.com/artidijital_trakyaturk/trakyaturk/playlist.m3u8"))
channelList.add(Channel("TRT 2", "https://tv-trt2.medya.trt.com.tr/master.m3u8"))
channelList.add(Channel("TRT 3", "https://tv-trt3.live.trt.com.tr/master.m3u8"))
channelList.add(Channel("TRT Avaz", "https://tv-trtavaz.medya.trt.com.tr/master.m3u8"))
channelList.add(Channel("TRT Haber", "https://tv-trthaber.medya.trt.com.tr/master.m3u8"))
channelList.add(Channel("TRT Kurdî", "https://tv-trtkurdi.medya.trt.com.tr/master.m3u8"))
channelList.add(Channel("TRT Müzik", "https://tv-trtmuzik.medya.trt.com.tr/master.m3u8"))
channelList.add(Channel("TRT Spor Yıldız", "https://tv-trtspor2.medya.trt.com.tr/master.m3u8"))
channelList.add(Channel("TRT Türk", "https://tv-trtturk.medya.trt.com.tr/master.m3u8"))
channelList.add(Channel("TRT World", "https://tv-trtworld.medya.trt.com.tr/master.m3u8"))
channelList.add(Channel("TürkHaber", "https://edge1.socialsmart.tv/turkhaber/bant1/playlist.m3u8"))
channelList.add(Channel("TV 1", "https://edge1.socialsmart.tv/tv1/bant1/playlist.m3u8"))
channelList.add(Channel("TV 4", "https://turkmedya-live.ercdn.net/tv4/tv4.m3u8"))
channelList.add(Channel("TV 38", "https://59cba4d34b678.streamlock.net/live/tv38/playlist.m3u8"))
channelList.add(Channel("TV 41", "https://live.artidijitalmedya.com/artidijital_tv41/tv41/playlist.m3u8"))
channelList.add(Channel("TV 52", "https://edge1.socialsmart.tv/tv52/bant1/playlist.m3u8"))
channelList.add(Channel("TV 264", "https://b01c02nl.mediatriple.net/videoonlylive/mtdxkkitgbrckilive/broadcast_5ee244263fd6d.smil/playlist.m3u8"))
channelList.add(Channel("TV Den", "http://canli.tvden.com.tr/hls/live.m3u8"))
channelList.add(Channel("Üniversite TV", "https://vdo.digitalbox.xyz:3986/live/unitvlive.m3u8"))
channelList.add(Channel("Urfa Natik TV", "https://live.artidijitalmedya.com/artidijital_urfanatiktv/urfanatiktv/playlist.m3u8"))
channelList.add(Channel("ÜÜ TV", "https://uskudarunv.mediatriple.net/uskudarunv/uskudar2/playlist.m3u8"))
channelList.add(Channel("V TV", "https://serdar.tiviplayer.com/player/m3u8/65aae71c9b93f0965aaed3a92660d98b/65aae71c9b93f0965aaed3a92660d98b.m3u8"))
channelList.add(Channel("Vav TV", "https://playlist.fasttvcdn.com/pl/rfrk9821hdy9dayo8wfyha/kltr-sanat-tv/playlist.m3u8"))
channelList.add(Channel("Viasat Explore", "https://tv.arectv29.sbs/live/viasathistory.m3u8"))
channelList.add(Channel("Vizyon 58 TV", "https://live.artidijitalmedya.com/artidijital_vizyon58/vizyon58/playlist.m3u8"))
channelList.add(Channel("Woman TV", "https://embedlp.becdn.net/womantv.m3u8"))
channelList.add(Channel("Zarok TV", "https://zindikurmanci.zaroktv.com.tr/hls/stream.m3u8"))

            }
        }
    }

    // --- KANAL NUMARASI TUŞLAMA MANTIĞI ---
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            val digit = keyCode - KeyEvent.KEYCODE_0
            inputBuffer.append(digit)
            
            handler.removeCallbacks(jumpRunnable)
            handler.postDelayed(jumpRunnable, 1200) // 1.2 saniye bekle
            
            Toast.makeText(this, "Numara: $inputBuffer", Toast.LENGTH_SHORT).show()
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

    // --- SÜRÜKLE-BIRAK VE YENİDEN NUMARALANDIRMA ---
    private fun setupDragAndDrop() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val from = vh.adapterPosition
                val to = target.adapterPosition
                
                Collections.swap(channelList, from, to)
                
                // Yer değişince numaraları (ID) tekrar güncelle
                reassignChannelIds()
                
                adapter.notifyItemMoved(from, to)
                // Numaralar değiştiği için listeyi tazele
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
        if (url.contains(".m3u8") || url.contains(".ts")) {
            val mediaItem = MediaItem.fromUri(url)
            previewPlayer?.setMediaItem(mediaItem)
            previewPlayer?.prepare()
            previewPlayer?.play()
        } else {
            previewPlayer?.stop()
        }
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
        val intent = if (url.contains(".m3u8") || url.contains(".ts")) {
            Intent(this, PlayerActivity::class.java)
        } else {
            Intent(this, WebPlayerActivity::class.java)
        }
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
                    1 -> {
                        channelList.removeAt(position)
                        reassignChannelIds() // Silince numaraları düzelt
                        adapter.notifyDataSetChanged()
                    }
                }
            }.show()
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
                    // Listeye eklerken yeni numara ver
                    channelList.add(Channel(channelList.size + 1, name, url))
                    adapter.notifyItemInserted(channelList.size - 1)
                }
            }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        previewPlayer?.release()
    }
}
