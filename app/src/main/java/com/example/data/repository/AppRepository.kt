package com.example.data.repository

import com.example.data.local.dao.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val availabilityDao: AvailabilityDao,
    private val eventDao: EventDao,
    private val scheduleDao: ScheduleDao,
    private val substitutionRequestDao: SubstitutionRequestDao,
    private val notificationDao: NotificationDao
) {
    fun getAllUsers() = userDao.getAllUsers()
    fun getUserById(id: Int) = userDao.getUserById(id)
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)

    fun getAvailabilities(userId: Int) = availabilityDao.getAvailabilitiesForUser(userId)
    fun getAllAvailabilities() = availabilityDao.getAllAvailabilities()
    suspend fun insertAvailability(a: Availability) = availabilityDao.insertAvailability(a)
    suspend fun updateAvailability(a: Availability) = availabilityDao.updateAvailability(a)

    fun getAllEvents() = eventDao.getAllEvents()
    fun getEventById(id: Int) = eventDao.getEventById(id)
    suspend fun insertEvent(e: Event) = eventDao.insertEvent(e)
    suspend fun updateEvent(e: Event) = eventDao.updateEvent(e)

    fun getSchedulesForUser(userId: Int) = scheduleDao.getSchedulesForUser(userId)
    fun getSchedulesForEvent(eventId: Int) = scheduleDao.getSchedulesForEvent(eventId)
    fun getAllSchedules() = scheduleDao.getAllSchedules()
    suspend fun insertSchedule(s: Schedule) = scheduleDao.insertSchedule(s)
    suspend fun updateSchedule(s: Schedule) = scheduleDao.updateSchedule(s)
    suspend fun deleteSchedule(s: Schedule) = scheduleDao.deleteSchedule(s)

    fun getNotifications(userId: Int) = notificationDao.getNotificationsForUser(userId)
    suspend fun insertNotification(n: Notification) = notificationDao.insertNotification(n)
    suspend fun updateNotification(n: Notification) = notificationDao.updateNotification(n)

    fun getAllSubstitutionRequests() = substitutionRequestDao.getAllRequests()
    suspend fun insertSubstitutionRequest(r: SubstitutionRequest) = substitutionRequestDao.insertRequest(r)
    suspend fun updateSubstitutionRequest(r: SubstitutionRequest) = substitutionRequestDao.updateRequest(r)
}
