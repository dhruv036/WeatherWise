package io.dhruv.weatherwise

import kotlinx.serialization.Serializable

@Serializable
data class ResponseModal(
    val coord: Coord = Coord(),
    val weather: List<Weather> = emptyList(),
    val base: String = "",
    val main: Main = Main(),
    val visibility: Long = 0L,
    val wind: Wind =  Wind(),
    val clouds: Clouds =  Clouds(),
    val dt: Long = 0L,
    val sys: Sys = Sys(),
    val timezone: Long = 0L,
    val id: Long = 0L,
    val name: String = "",
    val cod: Long = 0L,
)

@Serializable
data class Coord(
    val lon: Double = 0.0,
    val lat: Double = 0.0,
)

@Serializable
data class Weather(
    val id: Long = 0L,
    val main: String = "",
    val description: String = "",
    var icon: String = "",
)

@Serializable
data class Main(
    val temp: Double? = null,
    val feels_like: Double = 0.0,
    val temp_min: Double = 0.0,
    val temp_max: Double = 0.0,
    val pressure: Long = 0L,
    val humidity: Long = 0L,
    val sea_level: Long = 0L,
    val grnd_level: Long = 0L
)

@Serializable
data class Wind(
    val speed: Double = 0.0,
    val deg: Long = 0L,
    val gust: Double = 0.0,
)

@Serializable
data class Clouds(
    val all: Long = 0L,
)

@Serializable
data class Sys(
    val country: String ="",
    val sunrise: Long = 0L,
    val sunset: Long = 0L,
)
