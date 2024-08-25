package io.dhruv.weatherwise

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather?appid=a9d7877ecf28b142eab78b39d9e14c03")
    suspend fun getPosts(
        @Query("lat") lat: Double,
        @Query("lon") long: Double
    ): ResponseModal
}