package com.example.weathercompose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CityDao {
    @Query("SELECT * FROM saved_cities")
    fun getAllCities(): List<City>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCity(city: City)

}