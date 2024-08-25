package io.dhruv.weatherwise.data.network

import io.dhruv.weatherwise.data.model.ResponseModal
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun getPosts(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("appid") apiKey: String
    ): ResponseModal
}