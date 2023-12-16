package com.example.nearby.nearbylist.model.network

import com.example.nearby.nearbylist.model.view.NearbyEntity
import com.google.gson.annotations.SerializedName
import java.util.UUID

data class NearbyListResponse(
    @SerializedName("venues") val nearbyListItems: List<NearbyListResponseItem>? = null,
)

data class NearbyListResponseItem(
    @SerializedName("name") val name: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("display_location") val location: String? = null,
)

fun NearbyListResponseItem.toNearbyEntity() =
    NearbyEntity(
        uid = UUID.randomUUID().hashCode(),
        name = name.orEmpty(),
        url = url.orEmpty(),
        location = location.orEmpty(),
    )
