package com.arjun.lifehacks.di

import com.arjun.lifehacks.data.AuthRepositoryImpl
import com.arjun.lifehacks.domain.AuthRepository
import com.arjun.lifehacks.presentation.auth.create_account.CreateAccountViewModel
import com.arjun.lifehacks.presentation.auth.forgot.ForgotViewModel
import com.arjun.lifehacks.presentation.auth.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }





    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // ViewModels
    viewModel { CreateAccountViewModel(get()) }
    viewModel { ForgotViewModel(get()) }
    viewModel { LoginViewModel(get()) }
}
