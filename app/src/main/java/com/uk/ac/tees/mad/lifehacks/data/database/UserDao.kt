package com.uk.ac.tees.mad.lifehacks.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :uid")
    fun getUser(uid: String): Flow<UserEntity?>

    @Upsert
    suspend fun upsertUser(user: UserEntity)
}
