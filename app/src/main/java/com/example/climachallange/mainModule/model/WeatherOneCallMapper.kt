package com.example.climachallange.mainModule.model

import java.text.SimpleDateFormat
import java.util.*

class WeatherOneCallMapper : Mapper<WeatherOneCallDTO, WeatherOneCall>{
    override fun fromEntity(entity: WeatherOneCall): WeatherOneCallDTO {
        return WeatherOneCallDTO(
            name = entity.timezone,
            temp = entity.current?.temp.toString(),
            weatherCondition = entity.current?.weather?.get(0)?.description ?: "Sin dato",
            sunrise = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(entity.current?.sunrise!!)),
            sunset = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(entity.current.sunset)),
            feelsLike = "Feels Like: ${entity.current.feelsLike}°",
            wind = entity.current.windSpeed.toString(),
            pressure = entity.current.pressure.toString(),
            humidity = entity.current.humidity.toString(),
            date = SimpleDateFormat("EEE,d MMM", Locale.ENGLISH).format(Date()),
            imgWeatherCondition = "https://openweathermap.org/img/wn/${entity.current.weather[0].icon}@4x.png",
            daily = entity.daily
        )
    }

    override fun toEntity(domain: WeatherOneCallDTO): WeatherOneCall? = null
}
