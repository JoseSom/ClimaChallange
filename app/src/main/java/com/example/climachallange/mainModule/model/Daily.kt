package com.example.climachallange.mainModule.model

import com.google.gson.annotations.SerializedName

data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    @SerializedName("moon_phase")
    val moonPhase: Double,
    val temp: Temp,
    val feelsLike: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_deg")
    val windDeg: Int,
    val weather: List<Weather>,
    val clouds: Int,
    val pop: Double,
    val rain: Double,
    val uvi: Double
)
