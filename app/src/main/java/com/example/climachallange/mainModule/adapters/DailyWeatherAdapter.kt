package com.example.climachallange.mainModule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.climachallange.R
import com.example.climachallange.databinding.ItemWeatherBinding
import com.example.climachallange.mainModule.model.Daily

class DailyWeatherAdapter(private var dailys: List<Daily> ): RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder>(){
   private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_weather,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val daily = dailys[position]

        with(holder){
            binding.itemTvTemperatureScale.text = "°F"
            binding.itemTvVariable.text = daily.weather[0].description
            binding.itemTvTemperatureNumber.text = daily.temp.day.toString()
            Glide.with(binding.root)
                .load("https://openweathermap.org/img/wn/${daily.weather[0].icon}@2x.png")
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.itemIvWeatherCondition)
        }
    }

    override fun getItemCount(): Int = dailys.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ItemWeatherBinding.bind(view)
    }
}