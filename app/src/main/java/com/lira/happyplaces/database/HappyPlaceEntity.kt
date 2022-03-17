package com.lira.happyplaces.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "happyplace-table")
data class HappyPlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val image: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
    )
