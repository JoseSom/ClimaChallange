package com.example.climachallange.mainModule.model

import com.google.gson.annotations.SerializedName

data class Minutely(
    val dt:Long,
    val precipitation: Double
)
