package com.example.climachallange.mainModule.model

import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("1h")
    val hour: Double
)
