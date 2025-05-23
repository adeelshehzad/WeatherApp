package com.example.weathercompose.data.local

import android.content.Context
import androidx.room.Room

object CityDatabaseProvider {
    private var INSTANCE: CityDatabase? = null

    fun getCityData(context: Context): CityDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context = context.applicationContext,
                klass = CityDatabase::class.java,
                name = "city_database"
            ).build()

            INSTANCE = instance
            instance
        }
    }
}