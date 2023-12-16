package com.example.nearby.network

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nearby.nearbylist.model.view.NearbyEntity

@Database(entities = [NearbyEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun nearbyDao(): NearbyDao
}