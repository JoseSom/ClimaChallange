package com.example.climachallange.mainModule.model

import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.*

class WeatherEntityMapper : Mapper<WeatherEntityDTO, WeatherEntity> {
    override fun fromEntity(entity: WeatherEntity): WeatherEntityDTO {
        return WeatherEntityDTO(
            name = entity.name + "," + entity.sys.country ,
            temp = entity.main.temp.toInt(),
            weatherCondition = entity.weather[0].description,
            sunrise = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(entity.sys.sunrise)),
            sunset = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(entity.sys.sunset)),
            feelsLike = "Feels Like: ${entity.main.feelsLike}°",
            wind = entity.wind.speed.toString(),
            pressure = entity.main.pressure.toString(),
            humidity = entity.main.humidity.toString(),
            date = SimpleDateFormat("EEE,d MMM", Locale.ENGLISH).format(Date()),
            imgWeatherCondition = "https://openweathermap.org/img/wn/${entity.weather[0].icon}@4x.png"
        )
    }

    override fun toEntity(domain: WeatherEntityDTO): WeatherEntity? = null
}