package com.turkbox.tv

import com.google.gson.annotations.SerializedName

data class Channel(
    @SerializedName("name") var name: String,
    @SerializedName("url") var url: String,
    @SerializedName("logo") var logo: String? = null,
    var id: Int = 0
)
