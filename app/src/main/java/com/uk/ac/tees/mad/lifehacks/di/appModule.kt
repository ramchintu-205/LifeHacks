package com.uk.ac.tees.mad.lifehacks.di


import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uk.ac.tees.mad.lifehacks.data.AdviceSlipRepository
import com.uk.ac.tees.mad.lifehacks.data.AdviceSlipRepositoryImpl
import com.uk.ac.tees.mad.lifehacks.data.AuthRepositoryImpl
import com.uk.ac.tees.mad.lifehacks.data.UserRepository
import com.uk.ac.tees.mad.lifehacks.data.UserRepositoryImpl
import com.uk.ac.tees.mad.lifehacks.data.database.LifeHacksDatabase
import com.uk.ac.tees.mad.lifehacks.domain.AuthRepository
import com.uk.ac.tees.mad.lifehacks.presentation.auth.create_account.CreateAccountViewModel
import com.uk.ac.tees.mad.lifehacks.presentation.auth.forgot.ForgotViewModel
import com.uk.ac.tees.mad.lifehacks.presentation.auth.login.LoginViewModel
import com.uk.ac.tees.mad.lifehacks.presentation.favourite.FavouriteViewModel
import com.uk.ac.tees.mad.lifehacks.presentation.home.HomeViewModel
import com.uk.ac.tees.mad.lifehacks.presentation.profile.ProfileViewModel
import com.uk.ac.tees.mad.lifehacks.presentation.splash.SplashViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
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

    // Supabase
    single {
        createSupabaseClient(
            supabaseUrl = "https://sersdqnxqnqrgpfsduiq.supabase.co",
            supabaseKey = "sb_publishable_T5sz-oCDXC_VQLFXOOOYzg_QBmxbVyE"
        ) {
            install(Storage)
        }
    }

    single { get<SupabaseClient>().storage }

    // Ktor HttpClient
    single {
        HttpClient {
            install(HttpTimeout){
                requestTimeoutMillis = 30000  // 30 seconds
                connectTimeoutMillis = 30000  // 30 seconds
                socketTimeoutMillis = 30000   // 30 seconds
            }

            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = true
                    },
                    contentType = ContentType.Any,

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
        ).fallbackToDestructiveMigration().build()
    }

    // DAO
    single { get<LifeHacksDatabase>().lifeHackDao() }
    single { get<LifeHacksDatabase>().userDao() }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<AdviceSlipRepository> { AdviceSlipRepositoryImpl(get(), get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get(), get(), get(), androidContext()) }


    // ViewModels
    viewModel { CreateAccountViewModel(get()) }
    viewModel { ForgotViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { SplashViewModel(get(), get(), androidContext()) }
    viewModel { FavouriteViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
}
