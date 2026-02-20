// Bu kod kanalları ekranda liste halinde gösterir
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Kanal listesini GitHub'dan çekme ve ekrana basma işlemi burada yapılır
        setupUI()
    }
    
    private fun setupUI() {
        // Android TV (Leanback) arayüzü bileşenleri
    }
}
