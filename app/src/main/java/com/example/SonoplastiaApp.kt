package com.example

import android.app.Application

class SonoplastiaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}
