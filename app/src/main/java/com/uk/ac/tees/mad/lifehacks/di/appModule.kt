package com.uk.ac.tees.mad.lifehacks.di


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uk.ac.tees.mad.lifehacks.data.AuthRepositoryImpl
import com.uk.ac.tees.mad.lifehacks.domain.AuthRepository
import com.uk.ac.tees.mad.lifehacks.presentation.auth.create_account.CreateAccountViewModel
import com.uk.ac.tees.mad.lifehacks.presentation.auth.forgot.ForgotViewModel
import com.uk.ac.tees.mad.lifehacks.presentation.auth.login.LoginViewModel
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
