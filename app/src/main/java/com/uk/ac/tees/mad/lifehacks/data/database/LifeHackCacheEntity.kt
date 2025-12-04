package com.uk.ac.tees.mad.lifehacks.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uk.ac.tees.mad.lifehacks.presentation.home.LifeHack

@Entity(tableName = "life_hacks")
data class LifeHackCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val category: String,
    val description: String,
    val imageUrl: String?,
    val source: String,
    val timestamp: Long
)

fun LifeHackCacheEntity.toLifeHack(): LifeHack {
    return LifeHack(
        title = title,
        category = category,
        description = description,
        imageUrl = imageUrl,
        source = source,
        isFavorite = false
    )
}

fun LifeHack.toCacheEntity(): LifeHackCacheEntity {
    return LifeHackCacheEntity(
        title = title,
        category = category,
        description = description,
        imageUrl = imageUrl,
        source = source,
        timestamp = System.currentTimeMillis()
    )
}
