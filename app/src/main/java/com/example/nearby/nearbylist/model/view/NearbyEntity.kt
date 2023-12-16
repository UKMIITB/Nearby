package com.example.nearby.nearbylist.model.view

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nearby.nearbylist.util.NearbyConstants.NEARBY_TABLE_NAME

@Entity(tableName = NEARBY_TABLE_NAME)
data class NearbyEntity(
    @PrimaryKey val uid: Int,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("url") val url: String,
    @ColumnInfo("location") val location: String,
)
