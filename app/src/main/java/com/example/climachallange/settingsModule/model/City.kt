package com.example.climachallange.settingsModule.model

import com.example.climachallange.mainModule.model.Coordinate

data class City (
    val id: Long,
    val name: String,
    val state: String,
    val country: String,
    val coordinate: Coordinate
)
