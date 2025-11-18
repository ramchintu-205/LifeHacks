package com.arjun.lifehacks

import android.app.Application
import com.arjun.lifehacks.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LifeHacksApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@LifeHacksApplication)
            modules(appModule)
        }
    }
}
