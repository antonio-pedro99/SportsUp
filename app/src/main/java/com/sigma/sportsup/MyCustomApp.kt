package com.sigma.sportsup

import android.app.Application
import com.google.firebase.FirebaseApp

class MyCustomApp:Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}