package com.uk.ac.tees.mad.lifehacks.data

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uk.ac.tees.mad.lifehacks.data.database.UserDao
import com.uk.ac.tees.mad.lifehacks.data.database.UserEntity
import com.uk.ac.tees.mad.lifehacks.domain.util.Result
import com.uk.ac.tees.mad.lifehacks.domain.util.DataError
import com.uk.ac.tees.mad.lifehacks.presentation.profile.User
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: Storage,
    private val context: Context
) : UserRepository {

    override fun getUserData(): Flow<User?> {
        val uid = firebaseAuth.currentUser?.uid ?: return kotlinx.coroutines.flow.flowOf(null)
        return userDao.getUser(uid).map { it?.toUser() }
    }

    override suspend fun uploadProfilePicture(uri: Uri): Result<String, DataError.Remote> {
        return try {
            val userId = firebaseAuth.currentUser?.uid ?: return Result.Failure(DataError.Remote.UNAUTHORIZED)
            val fileName = "${userId}-${UUID.randomUUID()}"
            val fileBytes = context.contentResolver.openInputStream(uri)?.readBytes()
            fileBytes?.let {
                val bucket = storage["profile-pictures"]
                bucket.upload(path = fileName, data = it, options = { upsert = true })
                val publicUrl = bucket.publicUrl(path = fileName)
                Result.Success(publicUrl)
            } ?: Result.Failure(DataError.Remote.UNKNOWN)
        } catch (e: Exception) {
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun saveProfilePictureUrl(url: String): Result<Unit, DataError.Firebase> {
        return try {
            val userId = firebaseAuth.currentUser?.uid ?: return Result.Failure(DataError.Firebase.UNKNOWN)
            firestore.collection("users").document(userId).update("profilePictureUrl", url).await()

            val currentUser = userDao.getUser(userId).firstOrNull()
            currentUser?.let {
                val updatedUser = it.copy(profilePictureUrl = url)
                userDao.upsertUser(updatedUser)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(DataError.Firebase.UNKNOWN)
        }
    }

    override suspend fun syncUser() {
        try {
            firebaseAuth.currentUser?.let { firebaseUser ->
                val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
                val user = UserEntity(
                    id = firebaseUser.uid,
                    name = userDoc.getString("name") ?: "",
                    email = userDoc.getString("email") ?: "",
                    profilePictureUrl = userDoc.getString("profilePictureUrl") ?: ""
                )
                userDao.upsertUser(user)
            }
        } catch (e: Exception) {
            // Optionally log the error
        }
    }

    private fun UserEntity.toUser() = User(id = id, name = name, email = email, profilePictureUrl = profilePictureUrl)
}
