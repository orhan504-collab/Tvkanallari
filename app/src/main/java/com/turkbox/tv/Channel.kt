package com.turkbox.tv

import com.google.gson.annotations.SerializedName

data class Channel(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String,
    @SerializedName("logo") val logo: String? = null // Logo eklemek istersen hazır olur
)
