package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*

@Database(
    entities = [
        User::class,
        Availability::class,
        Event::class,
        Schedule::class,
        SubstitutionRequest::class,
        Notification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun availabilityDao(): AvailabilityDao
    abstract fun eventDao(): EventDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun substitutionRequestDao(): SubstitutionRequestDao
    abstract fun notificationDao(): NotificationDao
}
