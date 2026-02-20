import retrofit2.http.GET
import retrofit2.Call

interface ApiService {
    // GitHub'daki channels.json dosyasının "Raw" linkinin son kısmını buraya yazacağız
    @GET("ana-dizin/main/channels.json")
    fun getChannels(): Call<List<Channel>>
}
