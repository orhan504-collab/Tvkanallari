package com.turkbox.tv

import com.google.gson.annotations.SerializedName

data class Channel(
    var id: Int = 0, // Kanal numarası için ekledik
    @SerializedName("name") var name: String,
    @SerializedName("url") var url: String,
    @SerializedName("logo") var logo: String? = null
)
