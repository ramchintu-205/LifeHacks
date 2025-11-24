package com.uk.ac.tees.mad.lifehacks.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LifeHackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLifeHack(lifeHack: LifeHackCacheEntity)

    @Query("SELECT * FROM life_hacks ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLifeHack(): LifeHackCacheEntity?

    @Query("SELECT * FROM life_hacks ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomLifeHack(): LifeHackCacheEntity?
}
