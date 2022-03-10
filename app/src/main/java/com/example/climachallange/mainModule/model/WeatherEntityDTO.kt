package com.example.climachallange.mainModule.model

import android.service.notification.Condition
import com.google.gson.annotations.SerializedName

data class WeatherEntityDTO(
    val name: String,
    val temp: Int,
    val weatherCondition: String,
    val sunrise: String,
    val sunset: String,
    val feelsLike: String,
    val wind: String,
    val pressure: String,
    val humidity: String,
    val date: String,
    val imgWeatherCondition: String
)
