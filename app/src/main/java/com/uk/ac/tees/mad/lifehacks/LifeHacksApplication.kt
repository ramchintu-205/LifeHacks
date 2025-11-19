package com.uk.ac.tees.mad.lifehacks

import android.app.Application
import com.uk.ac.tees.mad.lifehacks.di.appModule
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
