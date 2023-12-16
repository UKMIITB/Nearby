package com.example.nearby.nearbylist.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.nearby.nearbylist.model.network.toNearbyEntity
import com.example.nearby.nearbylist.model.view.NearbyEntity

class NearbyPagingSource(
    private val nearbyListRepository: NearbyListRepository,
    private val range: String
) : PagingSource<Int, NearbyEntity>() {

    companion object {
        const val PER_PAGE_LIMIT = 10
        const val PAGINATION_DEFAULT_KEY_VALUE = 1
    }

    override fun getRefreshKey(state: PagingState<Int, NearbyEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NearbyEntity> {
        try {
            val currentKey = params.key ?: PAGINATION_DEFAULT_KEY_VALUE

            val nearbyPlacesAPIResponse = nearbyListRepository.getNearbyPlacesFromNetwork(
                perPage = PER_PAGE_LIMIT,
                page = currentKey,
                lat = 12.971599,
                lon = 77.594566,
                range = range,
                query = ""
            )

            if (!nearbyPlacesAPIResponse.isSuccessful) {
                return LoadResult.Error(Throwable("Some error message here"))
            }

            val nearbyPlacesList = nearbyPlacesAPIResponse.body()?.nearbyListItems ?: emptyList()

            val nearbyEntities = nearbyPlacesList.map { it.toNearbyEntity() }

            if (currentKey == PAGINATION_DEFAULT_KEY_VALUE) { // Caching the 1st page response
                nearbyListRepository.deleteAllAndInsertAllInCache(nearbyEntities)
            }

            return LoadResult.Page(
                data = nearbyEntities,
                prevKey = null,
                nextKey = if (nearbyPlacesList.size < PER_PAGE_LIMIT) null else currentKey + 1
            )

        } catch (exception: Exception) {
            return LoadResult.Error(throwable = exception)
        }
    }
}