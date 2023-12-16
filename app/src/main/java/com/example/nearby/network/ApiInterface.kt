package com.example.nearby.network

import com.example.nearby.nearbylist.model.network.NearbyListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NearbyApiService {

    @GET("2/venues")
    suspend fun getNearbyPlaces(
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("client_id") clientId: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("range") range: String,
        @Query("q") query: String
    ): Response<NearbyListResponse>
}