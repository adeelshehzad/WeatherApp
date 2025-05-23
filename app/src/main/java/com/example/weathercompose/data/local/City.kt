package com.example.weathercompose.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_cities")
data class City(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityName: String
)
