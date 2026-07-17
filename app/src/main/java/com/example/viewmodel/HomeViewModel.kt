package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.Event
import com.example.data.local.entity.Notification
import com.example.data.local.entity.Schedule
import com.example.data.local.entity.SubstitutionRequest
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat

class HomeViewModel(private val repository: AppRepository) : ViewModel() {

    private val _userId = MutableStateFlow<Int?>(null)
    val userId: StateFlow<Int?> = _userId
    
    fun setUserId(id: Int) {
        _userId.value = id
        loadData()
    }

    private val _mySchedules = MutableStateFlow<List<Schedule>>(emptyList())
    val mySchedules: StateFlow<List<Schedule>> = _mySchedules
    
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications
    
    // For admin
    val allSchedules = repository.getAllSchedules().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allUsers = repository.getAllUsers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allAvailabilities = repository.getAllAvailabilities().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allSubstitutionRequests = repository.getAllSubstitutionRequests().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Admin commands
    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            repository.insertSchedule(schedule)
        }
    }

    fun removeSchedule(schedule: Schedule) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
        }
    }

    fun updateScheduleStatus(schedule: Schedule, newStatus: String) {
        viewModelScope.launch {
            repository.updateSchedule(schedule.copy(status = newStatus))
        }
    }

    fun addNewEvent(event: Event) {
        viewModelScope.launch {
            repository.insertEvent(event)
        }
    }

    private fun loadData() {
        val uid = _userId.value ?: return
        viewModelScope.launch {
            repository.getSchedulesForUser(uid).collect { _mySchedules.value = it }
        }
        viewModelScope.launch {
            repository.getNotifications(uid).collect { _notifications.value = it }
        }
        viewModelScope.launch {
            repository.getAllEvents().collect { _events.value = it }
        }
    }
    
    fun createDefaultEvents() {
        viewModelScope.launch {
            // Demo standard events
            val e1 = Event(title = "Culto de Quarta", description = "Culto de Ensino", eventDate = System.currentTimeMillis(), startTime = "20:00", endTime = "22:00", requiredOperators = 2)
            val e2 = Event(title = "Culto de Domingo", description = "Culto de Celebração", eventDate = System.currentTimeMillis() + 86400000 * 4, startTime = "19:00", endTime = "21:30", requiredOperators = 3)
            repository.insertEvent(e1)
            repository.insertEvent(e2)
        }
    }

    fun toggleUserStatus(user: com.example.data.local.entity.User) {
        viewModelScope.launch {
            val newStatus = if (user.status == "ACTIVE") "INACTIVE" else "ACTIVE"
            repository.updateUser(user.copy(status = newStatus))
        }
    }

    fun updateUserLevel(user: com.example.data.local.entity.User, newLevel: String) {
        viewModelScope.launch {
            repository.updateUser(user.copy(level = newLevel))
        }
    }

    fun updateUserRole(user: com.example.data.local.entity.User, newRole: String) {
        viewModelScope.launch {
            repository.updateUser(user.copy(role = newRole))
        }
    }

    fun signUpForEvent(eventId: Int, userId: Int) {
        viewModelScope.launch {
            val alreadySignedUp = allSchedules.value.any { it.eventId == eventId && it.userId == userId }
            if (!alreadySignedUp) {
                val newSchedule = Schedule(
                    eventId = eventId,
                    userId = userId,
                    status = "CONFIRMADO"
                )
                repository.insertSchedule(newSchedule)
            }
        }
    }

    fun withdrawFromEvent(eventId: Int, userId: Int) {
        viewModelScope.launch {
            val match = allSchedules.value.find { it.eventId == eventId && it.userId == userId }
            if (match != null) {
                repository.deleteSchedule(match)
            }
        }
    }

    fun generateMonthlyServices(monthOffset: Int) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            if (monthOffset != 0) {
                calendar.add(Calendar.MONTH, monthOffset)
            }
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val targetMonth = calendar.get(Calendar.MONTH)
            
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            
            while (calendar.get(Calendar.MONTH) == targetMonth) {
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val dateString = sdf.format(calendar.time)
                
                // Read current events to check for duplicates
                val currentEvents = _events.value
                
                val hasWed = currentEvents.any { 
                    sdf.format(Date(it.eventDate)) == dateString && it.title == "Culto de Quarta" 
                }
                val hasSat = currentEvents.any { 
                    sdf.format(Date(it.eventDate)) == dateString && it.title == "Culto de Sábado" 
                }
                val hasJA = currentEvents.any { 
                    sdf.format(Date(it.eventDate)) == dateString && it.title == "Culto Jovem (JA)" 
                }
                val hasSun = currentEvents.any { 
                    sdf.format(Date(it.eventDate)) == dateString && it.title == "Culto de Domingo" 
                }
                
                when (dayOfWeek) {
                    Calendar.WEDNESDAY -> {
                        if (!hasWed) {
                            val event = Event(
                                title = "Culto de Quarta",
                                description = "Culto de Oração e Ensino",
                                eventDate = calendar.timeInMillis,
                                startTime = "20:00",
                                endTime = "21:00",
                                requiredOperators = 1
                            )
                            repository.insertEvent(event)
                        }
                    }
                    Calendar.SATURDAY -> {
                        if (!hasSat) {
                            val eventMorning = Event(
                                title = "Culto de Sábado",
                                description = "Adoração e Escola Sabatina",
                                eventDate = calendar.timeInMillis,
                                startTime = "09:00",
                                endTime = "11:45",
                                requiredOperators = 2
                            )
                            repository.insertEvent(eventMorning)
                        }
                        if (!hasJA) {
                            val eventJA = Event(
                                title = "Culto Jovem (JA)",
                                description = "Culto de Sábado à Tarde",
                                eventDate = calendar.timeInMillis,
                                startTime = "17:00",
                                endTime = "18:00",
                                requiredOperators = 1
                            )
                            repository.insertEvent(eventJA)
                        }
                    }
                    Calendar.SUNDAY -> {
                        if (!hasSun) {
                            val event = Event(
                                title = "Culto de Domingo",
                                description = "Culto de Evangelismo",
                                eventDate = calendar.timeInMillis,
                                startTime = "19:00",
                                endTime = "20:00",
                                requiredOperators = 2
                            )
                            repository.insertEvent(event)
                        }
                    }
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    fun requestSubstitution(schedule: Schedule, reason: String) {
        viewModelScope.launch {
            repository.updateSchedule(schedule.copy(status = "SUBSTITUICAO_SOLICITADA"))
            val request = SubstitutionRequest(
                scheduleId = schedule.id,
                requesterId = schedule.userId,
                reason = reason,
                status = "PENDING"
            )
            repository.insertSubstitutionRequest(request)
        }
    }

    fun approveSubstitutionRequest(request: SubstitutionRequest) {
        viewModelScope.launch {
            val sched = allSchedules.value.find { it.id == request.scheduleId }
            if (sched != null) {
                repository.deleteSchedule(sched)
                val eventName = events.value.find { it.id == sched.eventId }?.title ?: "Culto"
                repository.insertNotification(
                    Notification(
                        userId = request.requesterId,
                        title = "Substituição Aprovada",
                        message = "Sua solicitação de substituição para o '$eventName' foi aprovada e você foi liberado da escala.",
                        isRead = false
                    )
                )
            }
            repository.updateSubstitutionRequest(request.copy(status = "APPROVED"))
        }
    }

    fun rejectSubstitutionRequest(request: SubstitutionRequest) {
        viewModelScope.launch {
            val sched = allSchedules.value.find { it.id == request.scheduleId }
            if (sched != null) {
                repository.updateSchedule(sched.copy(status = "CONFIRMADO"))
                val eventName = events.value.find { it.id == sched.eventId }?.title ?: "Culto"
                repository.insertNotification(
                    Notification(
                        userId = request.requesterId,
                        title = "Substituição Recusada",
                        message = "Sua solicitação de substituição para o '$eventName' foi recusada pelo administrador.",
                        isRead = false
                    )
                )
            }
            repository.updateSubstitutionRequest(request.copy(status = "REJECTED"))
        }
    }

    fun sendAnnouncementToAll(title: String, message: String) {
        viewModelScope.launch {
            allUsers.value.forEach { user ->
                if (user.role == "VOLUNTEER") {
                    repository.insertNotification(
                        Notification(
                            userId = user.id,
                            title = title,
                            message = message,
                            isRead = false
                        )
                    )
                }
            }
        }
    }
}
