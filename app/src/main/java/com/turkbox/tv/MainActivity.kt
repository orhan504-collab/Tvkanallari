    private fun setupAdapter(fragment: BrowseSupportFragment) {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val listRowAdapter = ArrayObjectAdapter(CardPresenter())

        try {
            val jsonString = assets.open("channels.json").bufferedReader().use { it.readText() }
            // SENİN JSON YAPIN DİREKT ARRAY OLARAK BAŞLIYOR:
            val jsonArray = org.json.JSONArray(jsonString) 

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                listRowAdapter.add(Channel(
                    i, // ID eksik olduğu için index numarasını ID olarak atıyoruz
                    obj.getString("name"),
                    obj.getString("url"),
                    obj.getString("logo")
                ))
            }
        } catch (e: Exception) {
            listRowAdapter.add(Channel(0, "Hata: ${e.message}", "", ""))
        }

        rowsAdapter.add(ListRow(HeaderItem(0, "Canlı Kanallar"), listRowAdapter))
        fragment.adapter = rowsAdapter
    }
    
