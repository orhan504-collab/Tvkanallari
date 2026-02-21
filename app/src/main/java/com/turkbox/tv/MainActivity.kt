    private fun setupAdapter(fragment: BrowseSupportFragment) {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)

        try {
            val jsonString = assets.open("channels.json").bufferedReader().use { it.readText() }
            
            // DÜZELTME: Önce ana nesneyi al, sonra içindeki "channels" listesini çek
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray("channels")

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                listRowAdapter.add(Channel(
                    obj.optInt("id", i),
                    obj.getString("name"),
                    obj.getString("url"),
                    obj.optString("logo", "")
                ))
            }
        } catch (e: Exception) {
            // Hata olursa burada detaylıca görelim
            listRowAdapter.add(Channel(0, "Veri Hatası: ${e.localizedMessage}", "", ""))
        }

        val header = HeaderItem(0, "Canlı Yayınlar")
        rowsAdapter.add(ListRow(header, listRowAdapter))
        fragment.adapter = rowsAdapter
    }
    
