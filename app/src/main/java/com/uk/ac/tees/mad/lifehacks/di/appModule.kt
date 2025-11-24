package com.uk.ac.tees.mad.lifehacks.di


import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uk.ac.tees.mad.lifehacks.data.AdviceSlipRepository
import com.uk.ac.tees.mad.lifehacks.data.AdviceSlipRepositoryImpl
import com.uk.ac.tees.mad.lifehacks.data.AuthRepositoryImpl
import com.uk.ac.tees.mad.lifehacks.data.database.LifeHacksDatabase
import com.uk.ac.tees.mad.lifehacks.domain.AuthRepository
import com.uk.ac.tees.mad.lifehacks.presentation.auth.create_account.CreateAccountViewModel
import com.uk.ac.tees.mad.lifehacks.presentation.auth.forgot.ForgotViewModel
import com.uk.ac.tees.mad.lifehacks.presentation.auth.login.LoginViewModel
import com.uk.ac.tees.mad.lifehacks.presentation.home.HomeViewModel
import com.uk.ac.tees.mad.lifehacks.presentation.splash.SplashViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Ktor HttpClient
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = true
                    },
                    contentType = ContentType.Any
                )
            }
        }
    }

    // Room Database
    single {
        Room.databaseBuilder(
            androidApplication(),
            LifeHacksDatabase::class.java,
            "lifehacks.db"
        ).build()
    }

    // DAO
    single { get<LifeHacksDatabase>().lifeHackDao() }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<AdviceSlipRepository> { AdviceSlipRepositoryImpl(get(), get(), get(), get()) }


    // ViewModels
    viewModel { CreateAccountViewModel(get()) }
    viewModel { ForgotViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { SplashViewModel(get(), get(), androidContext()) }
}
