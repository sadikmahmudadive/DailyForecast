package com.example.dailyforecast

import android.os.Bundle
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dailyforecast.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //44fa3b961ea417681514127a12ab5da0
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchWeatherData("Dhaka")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null){
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getCurrentWeatherData(cityName, "44fa3b961ea417681514127a12ab5da0", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise
                    val sunSet = responseBody.sys.sunset
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    binding.temperature.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemperature.text = "Max temp: $maxTemp °C"
                    binding.minTemperature.text = "Min temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunrise.text = "$sunRise"
                    binding.sunset.text = "$sunSet"
                    binding.seaLevel.text = "$seaLevel hPa"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = "$cityName"
                    //Log.d("TAG", "onResponse: $temperature")

                    changeDesignAccrodingToWeathercondition(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun changeDesignAccrodingToWeathercondition(conditions : String) {
        when (conditions) {
            "Heavy Rain", "Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rainy_background)
                binding.imageViewCondition.setImageResource(R.drawable.rainey_cloud)
                binding.imageViewDescriptionBox.setImageResource(R.drawable.rainey_box)
                changeTextColorRain(R.color.purple)
                setCityNameIcon(R.drawable.ic_rainey_location)
                setLayoutBackground(R.drawable.rainey_shape_bg)
            }
            "Light Rain", "Drizzle" ->{
                binding.root.setBackgroundResource(R.drawable.rainy2_background)
                binding.imageViewCondition.setImageResource(R.drawable.rainey2_cloud)
                binding.imageViewDescriptionBox.setImageResource(R.drawable.rainey2_box)
                changeTextColorRain2(R.color.mint)
                setCityNameIcon(R.drawable.ic_rainey_location2)
                setLayoutBackground(R.drawable.rainey_shape_bg2)
            }
            "Showers", "Moderate Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rainy3_background)
                binding.imageViewCondition.setImageResource(R.drawable.rainey2_cloud)
                binding.imageViewDescriptionBox.setImageResource(R.drawable.rainey3_box)
                changeTextColorRain3(R.color.mint)
                setCityNameIcon(R.drawable.ic_rainey_location2)
                setLayoutBackground(R.drawable.rainey_shape_bg3)
            }
            "Haze", "Light Snow", "Cold" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.imageViewCondition.setImageResource(R.drawable.snow_cloud)
                binding.imageViewDescriptionBox.setImageResource(R.drawable.snow_box)
                changeTextColorSnow(R.color.light_sky)
                setCityNameIcon(R.drawable.ic_snow_location)
                setLayoutBackground(R.drawable.snow_shape_bg)
            }
            "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow2_background)
                binding.imageViewCondition.setImageResource(R.drawable.snow2_cloud)
                binding.imageViewDescriptionBox.setImageResource(R.drawable.snow2_box)
                changeTextColorSnow2(R.color.light_off_white)
                setCityNameIcon(R.drawable.ic_snow2_location)
                setLayoutBackground(R.drawable.snow2_shape_bg)
            }
        }
    }

    private fun setLayoutBackground(drawableResId: Int) {
        binding.layoutHumidity.setBackgroundResource(drawableResId)
        binding.layoutWindSpeed.setBackgroundResource(drawableResId)
        binding.layoutCondition.setBackgroundResource(drawableResId)
        binding.layoutSunset.setBackgroundResource(drawableResId)
        binding.layoutSunrise.setBackgroundResource(drawableResId)
        binding.layoutSeaLevel.setBackgroundResource(drawableResId)
        binding.mainLayout.setBackgroundResource(drawableResId)
    }

    private fun setCityNameIcon(drawableResId: Int) {
        val drawable = resources.getDrawable(drawableResId, null)
        binding.cityName.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }

    private fun changeTextColorRain(purple: Int) {
        val color = resources.getColor(R.color.purple, null)
        binding.today.setTextColor(color)
        binding.temperature.setTextColor(color)
        binding.weather.setTextColor(color)
        binding.maxTemperature.setTextColor(color)
        binding.minTemperature.setTextColor(color)
        binding.cityName.setTextColor(color)
        binding.day.setTextColor(color)
        binding.date.setTextColor(color)
    }

    private fun changeTextColorRain2(mint: Int) {
        val color = resources.getColor(R.color.mint, null)
        binding.today.setTextColor(color)
        binding.temperature.setTextColor(color)
        binding.weather.setTextColor(color)
        binding.maxTemperature.setTextColor(color)
        binding.minTemperature.setTextColor(color)
        binding.cityName.setTextColor(color)
        binding.day.setTextColor(color)
        binding.date.setTextColor(color)
    }

    private fun changeTextColorRain3(mint: Int) {
        val color = resources.getColor(R.color.mint, null)
        binding.today.setTextColor(color)
        binding.temperature.setTextColor(color)
        binding.weather.setTextColor(color)
        binding.maxTemperature.setTextColor(color)
        binding.minTemperature.setTextColor(color)
        binding.cityName.setTextColor(color)
        binding.day.setTextColor(color)
        binding.date.setTextColor(color)
    }

    private fun changeTextColorSnow(light_sky: Int) {
        val color = resources.getColor(R.color.light_sky, null)
        binding.today.setTextColor(color)
        binding.temperature.setTextColor(color)
        binding.weather.setTextColor(color)
        binding.maxTemperature.setTextColor(color)
        binding.minTemperature.setTextColor(color)
        binding.cityName.setTextColor(color)
        binding.day.setTextColor(color)
        binding.date.setTextColor(color)
    }
    private fun changeTextColorSnow2(light_off_white: Int) {
        val color = resources.getColor(R.color.light_off_white, null)
        binding.today.setTextColor(color)
        binding.temperature.setTextColor(color)
        binding.weather.setTextColor(color)
        binding.maxTemperature.setTextColor(color)
        binding.minTemperature.setTextColor(color)
        binding.cityName.setTextColor(color)
        binding.day.setTextColor(color)
        binding.date.setTextColor(color)
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}
