package com.example.climachallange.mainModule.model

data class WeatherOneCallDTO (
    val name: String,
    val temp: String,
    val weatherCondition: String,
    val sunrise: String,
    val sunset: String,
    val feelsLike: String,
    val wind: String,
    val pressure: String,
    val humidity: String,
    val date: String,
    val imgWeatherCondition: String,
    val daily: List<Daily>?
    )