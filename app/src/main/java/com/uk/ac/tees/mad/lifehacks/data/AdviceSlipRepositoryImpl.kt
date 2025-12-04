package com.uk.ac.tees.mad.lifehacks.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.toObject
import com.uk.ac.tees.mad.lifehacks.data.database.LifeHackDao
import com.uk.ac.tees.mad.lifehacks.data.database.toCacheEntity
import com.uk.ac.tees.mad.lifehacks.data.database.toLifeHack
import com.uk.ac.tees.mad.lifehacks.domain.util.Result
import com.uk.ac.tees.mad.lifehacks.domain.util.DataError
import com.uk.ac.tees.mad.lifehacks.domain.util.EmptyResult
import com.uk.ac.tees.mad.lifehacks.domain.util.firebaseResult
import com.uk.ac.tees.mad.lifehacks.presentation.favourite.Hack
import com.uk.ac.tees.mad.lifehacks.presentation.home.LifeHack
import io.ktor.client.HttpClient
import com.uk.ac.tees.mad.lifehacks.domain.util.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await


class AdviceSlipRepositoryImpl(
    private val client: HttpClient,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val lifeHackDao: LifeHackDao
) : AdviceSlipRepository {

    override suspend fun getRandomLifeHack(): Result<LifeHack, DataError.Remote> {
        val result = client.get<AdviceSlipResponse>("https://api.adviceslip.com/advice")

        return when (result) {
            is Result.Success -> {
                val lifeHack = LifeHack(
                    title = result.data.slip.advice,
                    category = "General",
                    description = result.data.slip.advice,
                    imageUrl = null,
                    source = "AdviceSlip API",
                    isFavorite = false
                )
                lifeHackDao.insertLifeHack(lifeHack.toCacheEntity())
                Result.Success(lifeHack)
            }
            is Result.Failure -> {
                val cachedLifeHack = lifeHackDao.getLatestLifeHack()
                if (cachedLifeHack != null) {
                    Result.Success(cachedLifeHack.toLifeHack())
                } else {
                    Result.Failure(result.error)
                }
            }
        }
    }

    override suspend fun saveAsFavorite(lifeHack: LifeHack): EmptyResult<DataError.Firebase> {
        return firebaseResult {
            val user = firebaseAuth.currentUser
            requireNotNull(user) {
                "User not logged in"
            }
            firestore
                .collection("users")
                .document(user.uid)
                .collection("favorites")
                .document(lifeHack.title)
                .set(lifeHack)
                .await()
        }
    }

    override fun getFavorites(): Flow<List<Hack>> {
        val user = firebaseAuth.currentUser
        requireNotNull(user) {
            "User not logged in"
        }
        return firestore
            .collection("users")
            .document(user.uid)
            .collection("favorites")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject<Hack>() }
            }
    }

    override fun getFavoritesFromCache(): Flow<List<Hack>> {
        return lifeHackDao.getFavorites().map { list ->
            list.map { 
                Hack(
                    title = it.title,
                    description = it.description,
                    category = it.category,
                    imageUrl = it.imageUrl,
                    isFavorite = true,
                    source = it.source
                )
            }
        }
    }

    suspend fun removeFavorite(hack: Hack): EmptyResult<DataError.Firebase> {
        return firebaseResult {
            val user = firebaseAuth.currentUser
            requireNotNull(user) {
                "User not logged in"
            }
            firestore
                .collection("users")
                .document(user.uid)
                .collection("favorites")
                .document(hack.title)
                .delete()
                .await()
        }
    }
}
