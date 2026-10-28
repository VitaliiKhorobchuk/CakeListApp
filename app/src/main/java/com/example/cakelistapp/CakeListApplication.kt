package com.example.cakelistapp

import android.app.Application
import com.example.cakelistapp.di.AppContainer

class CakeListApplication : Application() {
    val container: AppContainer by lazy { AppContainer() }
}
