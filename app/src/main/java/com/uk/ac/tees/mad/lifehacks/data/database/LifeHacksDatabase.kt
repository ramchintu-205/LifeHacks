package com.uk.ac.tees.mad.lifehacks.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LifeHackCacheEntity::class, UserEntity::class],
    version = 2
)
abstract class LifeHacksDatabase : RoomDatabase() {
    abstract fun lifeHackDao(): LifeHackDao
    abstract fun userDao(): UserDao
}
