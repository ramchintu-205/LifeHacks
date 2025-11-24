package com.uk.ac.tees.mad.lifehacks.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LifeHackCacheEntity::class],
    version = 1
)
abstract class LifeHacksDatabase : RoomDatabase() {
    abstract fun lifeHackDao(): LifeHackDao
}
