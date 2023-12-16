package com.example.nearby.nearbylist.repository

import com.example.nearby.nearbylist.model.view.NearbyEntity
import com.example.nearby.nearbylist.util.NearbyConstants.NEARBY_API_CLIENT_ID
import com.example.nearby.network.NearbyApiService
import com.example.nearby.network.NearbyDao
import javax.inject.Inject

class NearbyListRepository @Inject constructor(
    private val nearbyDao: NearbyDao,
    private val nearbyApiService: NearbyApiService
) {

    suspend fun getNearbyPlacesCachedResponse() = nearbyDao.getAll()

    suspend fun deleteAllAndInsertAllInCache(nearbyEntities: List<NearbyEntity>) =
        nearbyDao.deleteAllAndInsertAll(nearbyEntities)

    suspend fun getNearbyPlacesFromNetwork(
        perPage: Int,
        page: Int,
        lat: Double,
        lon: Double,
        range: String,
        query: String,
    ) = nearbyApiService.getNearbyPlaces(
        perPage = perPage,
        page = page,
        clientId = NEARBY_API_CLIENT_ID,
        lat = lat,
        lon = lon,
        range = range,
        query = query
    )
}