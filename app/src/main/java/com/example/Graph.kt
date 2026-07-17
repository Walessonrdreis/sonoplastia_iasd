package com.example

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.repository.AppRepository

object Graph {
    lateinit var database: AppDatabase
        private set

    val repository by lazy {
        AppRepository(
            userDao = database.userDao(),
            availabilityDao = database.availabilityDao(),
            eventDao = database.eventDao(),
            scheduleDao = database.scheduleDao(),
            substitutionRequestDao = database.substitutionRequestDao(),
            notificationDao = database.notificationDao()
        )
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, AppDatabase::class.java, "sonoplastia.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}
