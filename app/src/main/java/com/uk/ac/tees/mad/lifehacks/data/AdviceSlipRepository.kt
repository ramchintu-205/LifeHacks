package com.uk.ac.tees.mad.lifehacks.data

import com.uk.ac.tees.mad.lifehacks.domain.util.Result
import com.uk.ac.tees.mad.lifehacks.domain.util.DataError
import com.uk.ac.tees.mad.lifehacks.domain.util.EmptyResult
import com.uk.ac.tees.mad.lifehacks.presentation.favourite.Hack
import com.uk.ac.tees.mad.lifehacks.presentation.home.LifeHack
import kotlinx.coroutines.flow.Flow

interface AdviceSlipRepository {
    suspend fun getRandomLifeHack(): Result<LifeHack, DataError.Remote>
    suspend fun saveAsFavorite(lifeHack: LifeHack): EmptyResult<DataError.Firebase>
    fun getFavorites(): Flow<List<Hack>>
    fun getFavoritesFromCache(): Flow<List<Hack>>
}