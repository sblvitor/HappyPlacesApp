package com.lira.happyplaces.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HappyPlaceDao {

    @Insert
    suspend fun insert(happyPlaceEntity: HappyPlaceEntity)

    @Update
    suspend fun update(happyPlaceEntity: HappyPlaceEntity)

    @Delete
    suspend fun delete(happyPlaceEntity: HappyPlaceEntity)

    @Query("SELECT * FROM `happyplace-table`")
    fun fetchAllHappyPlaces():Flow<List<HappyPlaceEntity>>

    @Query("SELECT * FROM `happyplace-table` WHERE id = :id")
    fun fetchHappyPlaceById(id: Int):Flow<HappyPlaceEntity>

}