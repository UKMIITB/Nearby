package com.example.nearby.network.di

import android.content.Context
import androidx.room.Room
import com.example.nearby.nearbylist.util.NearbyConstants.APP_DATABASE_NAME
import com.example.nearby.nearbylist.util.NearbyConstants.NEARBY_API_BASE_URL
import com.example.nearby.nearbylist.util.PermissionUtil
import com.example.nearby.nearbylist.util.PermissionUtilImpl
import com.example.nearby.network.AppDatabase
import com.example.nearby.network.NearbyApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesRetrofitInstance(): Retrofit =
        Retrofit.Builder()
            .baseUrl(NEARBY_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun providesNearbyApiService(retrofit: Retrofit): NearbyApiService =
        retrofit.create(NearbyApiService::class.java)

    @Singleton
    @Provides
    fun providesAppDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        APP_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun providesNearbyDao(appDatabase: AppDatabase) = appDatabase.nearbyDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppInterfaceModule {

    @Singleton
    @Binds
    abstract fun bindPermissionUtil(
        permissionUtilImpl: PermissionUtilImpl
    ): PermissionUtil
}