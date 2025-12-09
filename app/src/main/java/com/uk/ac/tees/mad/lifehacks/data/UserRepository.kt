package com.uk.ac.tees.mad.lifehacks.data

import android.net.Uri
import com.uk.ac.tees.mad.lifehacks.domain.util.Result
import com.uk.ac.tees.mad.lifehacks.domain.util.DataError
import com.uk.ac.tees.mad.lifehacks.presentation.profile.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserData(): Flow<User?>
    suspend fun uploadProfilePicture(uri: Uri): Result<String, DataError.Remote>
    suspend fun saveProfilePictureUrl(url: String): Result<Unit, DataError.Firebase>
    suspend fun syncUser()
}
