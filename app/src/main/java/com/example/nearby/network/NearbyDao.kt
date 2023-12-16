package com.example.nearby.network

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.nearby.nearbylist.model.view.NearbyEntity
import com.example.nearby.nearbylist.util.NearbyConstants

@Dao
interface NearbyDao {

    @Query("SELECT * FROM ${NearbyConstants.NEARBY_TABLE_NAME}")
    suspend fun getAll(): List<NearbyEntity>

    @Insert
    suspend fun insertAll(nearbyEntities: List<NearbyEntity>)

    @Query("DELETE FROM ${NearbyConstants.NEARBY_TABLE_NAME}")
    suspend fun deleteAll()

    @Transaction
    suspend fun deleteAllAndInsertAll(nearbyEntities: List<NearbyEntity>) {
        deleteAll()
        insertAll(nearbyEntities)
    }
}