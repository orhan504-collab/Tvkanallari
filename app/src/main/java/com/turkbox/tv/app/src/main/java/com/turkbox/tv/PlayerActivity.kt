import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class PlayerActivity : Activity() {
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val videoUrl = intent.getStringExtra("VIDEO_URL")
        player = ExoPlayer.Builder(this).build()
        val playerView = findViewById<StyledPlayerView>(R.id.player_view)
        playerView.player = player

        val mediaItem = MediaItem.fromUri(videoUrl!!)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }
}
