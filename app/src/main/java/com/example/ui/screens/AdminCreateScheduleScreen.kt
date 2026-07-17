package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.Event
import com.example.data.local.entity.Schedule
import com.example.data.local.entity.User
import com.example.data.local.entity.Availability
import com.example.viewmodel.HomeViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCreateScheduleScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val events by viewModel.events.collectAsState()
    val allSchedules by viewModel.allSchedules.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allAvailabilities by viewModel.allAvailabilities.collectAsState()

    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var showCreateEventDialog by remember { mutableStateOf(false) }
    var showGenerateMonthDialog by remember { mutableStateOf(false) }
    var showAddOperatorDialog by remember { mutableStateOf(false) }
    var showAutoSuggestMode by remember { mutableStateOf(true) } // Suggestion list vs Manual list

    Scaffold(
        containerColor = BgDark,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 40.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurfaceDark)
                        .border(1.dp, BorderSubtle, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "GERENCIAMENTO",
                        color = PrimaryIndigoLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Criar Escala",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Event Selector Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EVENTOS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { showGenerateMonthDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryIndigoLight)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gerar Mês", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = { showCreateEventDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryIndigoLight)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Novo Evento", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Events List
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                    .padding(12.dp)
            ) {
                if (events.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nenhum evento disponível", color = TextTertiary, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(events) { event ->
                            val eventSchedules = allSchedules.filter { it.eventId == event.id }
                            val isSelected = selectedEvent?.id == event.id
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val dateStr = sdf.format(Date(event.eventDate))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) PrimaryIndigoDark.copy(alpha = 0.3f) else androidx.compose.ui.graphics.Color.Transparent)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) PrimaryIndigoLight else androidx.compose.ui.graphics.Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedEvent = event }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = event.title,
                                        color = if (isSelected) PrimaryIndigoLight else TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "$dateStr • ${event.startTime} - ${event.endTime}",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (eventSchedules.size >= event.requiredOperators) EmeraldSuccess.copy(alpha = 0.1f) else AmberWarning.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${eventSchedules.size}/${event.requiredOperators}",
                                        color = if (eventSchedules.size >= event.requiredOperators) EmeraldSuccess else AmberWarning,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Scheduling Detail Area for selected Event
            val currentEvent = selectedEvent
            if (currentEvent == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Selecione um evento para ver e gerenciar as escalas",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Text(
                    text = "VAGAS E OPERADORES",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                val eventSchedules = allSchedules.filter { it.eventId == currentEvent.id }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Generate rows up to the required slots
                    items(List(currentEvent.requiredOperators) { it }) { index ->
                        val schedule = eventSchedules.getOrNull(index)
                        if (schedule != null) {
                            val user = allUsers.find { it.id == schedule.userId }
                            if (user != null) {
                                ScheduledOperatorCard(
                                    user = user,
                                    schedule = schedule,
                                    onRemove = { viewModel.removeSchedule(schedule) },
                                    onStatusChange = { newStatus -> viewModel.updateScheduleStatus(schedule, newStatus) }
                                )
                            } else {
                                EmptySlotCard(index = index + 1) {
                                    showAddOperatorDialog = true
                                }
                            }
                        } else {
                            EmptySlotCard(index = index + 1) {
                                showAddOperatorDialog = true
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal/Dialog for Creating an Event
    if (showCreateEventDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var dateStr by remember { mutableStateOf("") }
        var startTime by remember { mutableStateOf("") }
        var endTime by remember { mutableStateOf("") }
        var requiredOps by remember { mutableStateOf("2") }

        AlertDialog(
            onDismissRequest = { showCreateEventDialog = false },
            containerColor = SurfaceDark,
            title = {
                Text("Criar Novo Evento", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryIndigoLight,
                    unfocusedBorderColor = BorderMedium,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = PrimaryIndigoLight,
                    focusedLabelColor = PrimaryIndigoLight,
                    unfocusedLabelColor = TextSecondary
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Nome do Culto/Evento") },
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descrição (opcional)") },
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = dateStr,
                            onValueChange = { dateStr = it },
                            label = { Text("Data (ex: 05/07/2026)") },
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = startTime,
                                onValueChange = { startTime = it },
                                label = { Text("Início (19:00)") },
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = endTime,
                                onValueChange = { endTime = it },
                                label = { Text("Fim (21:00)") },
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = requiredOps,
                            onValueChange = { requiredOps = it },
                            label = { Text("Quantidade de Sonoplastas") },
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val parsedDate = try {
                            sdf.parse(dateStr)?.time ?: System.currentTimeMillis()
                        } catch (e: Exception) {
                            System.currentTimeMillis()
                        }

                        val event = Event(
                            title = title.ifEmpty { "Culto Especial" },
                            description = description,
                            eventDate = parsedDate,
                            startTime = startTime.ifEmpty { "19:00" },
                            endTime = endTime.ifEmpty { "21:00" },
                            requiredOperators = requiredOps.toIntOrNull() ?: 2
                        )
                        viewModel.addNewEvent(event)
                        showCreateEventDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Text("Adicionar", color = TextPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateEventDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }

    // Modal/Dialog for Selecting/Scheduling an Operator
    if (showAddOperatorDialog && selectedEvent != null) {
        val event = selectedEvent!!
        
        // Let's implement the Suggestion Algorithm
        // 1. Get Day of Week
        val calendar = Calendar.getInstance().apply { timeInMillis = event.eventDate }
        val eventDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Helper: Convert time HH:mm to minutes
        fun timeToMinutes(time: String): Int {
            val parts = time.split(":")
            if (parts.size < 2) return 0
            val hours = parts[0].toIntOrNull() ?: 0
            val mins = parts[1].toIntOrNull() ?: 0
            return hours * 60 + mins
        }

        val eventStartMins = timeToMinutes(event.startTime)
        val eventEndMins = timeToMinutes(event.endTime)

        // Calculate overlap
        fun isOverlapping(start1: Int, end1: Int, start2: Int, end2: Int): Boolean {
            return Math.max(start1, start2) < Math.min(end1, end2)
        }

        // 2. Active users
        val activeUsers = allUsers.filter { it.status == "ACTIVE" }

        // Compile suggestions
        val suggestions = activeUsers.map { user ->
            // Availability match
            val userAvailabilities = allAvailabilities.filter { it.userId == user.id && it.dayOfWeek == eventDayOfWeek && it.isAvailable }
            val hasAvailabilityMatch = userAvailabilities.any { avail ->
                val availStart = timeToMinutes(avail.startTime)
                val availEnd = timeToMinutes(avail.endTime)
                availStart <= eventStartMins && availEnd >= eventEndMins
            }

            // Conflict match (has another schedule overlapping on the exact same date)
            val userSchedules = allSchedules.filter { it.userId == user.id }
            val hasConflict = userSchedules.any { sched ->
                val schedEvent = events.find { it.id == sched.eventId }
                if (schedEvent != null) {
                    val sameDay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(schedEvent.eventDate)) ==
                                  SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(event.eventDate))
                    if (sameDay) {
                        val schedStart = timeToMinutes(schedEvent.startTime)
                        val schedEnd = timeToMinutes(schedEvent.endTime)
                        isOverlapping(eventStartMins, eventEndMins, schedStart, schedEnd)
                    } else false
                } else false
            }

            // Equidade: Scale count
            val scaleCount = userSchedules.size

            UserSuggestion(
                user = user,
                isAvailable = hasAvailabilityMatch,
                hasConflict = hasConflict,
                scaleCount = scaleCount
            )
        }

        // Sort Suggestions
        // Priority 1: No conflicts & Has Availability Match (Ascending scaleCount)
        // Priority 2: No conflicts & No registered Availability (Ascending scaleCount)
        // Priority 3: Has conflict (or inactive, but active filter is already applied)
        val sortedSuggestions = suggestions.sortedWith(
            compareBy<UserSuggestion> { it.hasConflict }
                .thenByDescending { it.isAvailable }
                .thenBy { it.scaleCount }
        )

        AlertDialog(
            onDismissRequest = { showAddOperatorDialog = false },
            containerColor = SurfaceDark,
            title = {
                Column {
                    Text("Adicionar Operador", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    TabRow(
                        selectedTabIndex = if (showAutoSuggestMode) 0 else 1,
                        containerColor = SurfaceDark,
                        contentColor = PrimaryIndigoLight,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[if (showAutoSuggestMode) 0 else 1]),
                                color = PrimaryIndigoLight
                            )
                        }
                    ) {
                        Tab(
                            selected = showAutoSuggestMode,
                            onClick = { showAutoSuggestMode = true },
                            text = { Text("SUGERIDOS (IA)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = !showAutoSuggestMode,
                            onClick = { showAutoSuggestMode = false },
                            text = { Text("TODOS OS VOLUNTÁRIOS", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }
            },
            text = {
                Box(modifier = Modifier.height(300.dp)) {
                    if (showAutoSuggestMode) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(sortedSuggestions) { suggestion ->
                                SuggestionItemCard(
                                    suggestion = suggestion,
                                    onSelect = {
                                        val sched = Schedule(
                                            eventId = event.id,
                                            userId = suggestion.user.id,
                                            status = "ESCALADO"
                                        )
                                        viewModel.addSchedule(sched)
                                        showAddOperatorDialog = false
                                    }
                                )
                            }
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(activeUsers) { user ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SurfaceDark)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                        .clickable {
                                            val sched = Schedule(
                                                eventId = event.id,
                                                userId = user.id,
                                                status = "ESCALADO"
                                            )
                                            viewModel.addSchedule(sched)
                                            showAddOperatorDialog = false
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryIndigoLight.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(user.name.take(1).uppercase(), color = PrimaryIndigoLight, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(user.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Text(user.level, color = TextSecondary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddOperatorDialog = false }) {
                    Text("Fechar", color = TextSecondary)
                }
            }
        )
    }

    // Modal/Dialog for Generating Automatic Monthly Scale Template
    if (showGenerateMonthDialog) {
        var selectedOffset by remember { mutableStateOf(0) } // 0 = current month, 1 = next month
        val calendar = Calendar.getInstance()
        val currentMonthName = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(calendar.time).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() }
        val nextCalendar = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }
        val nextMonthName = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(nextCalendar.time).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() }

        AlertDialog(
            onDismissRequest = { showGenerateMonthDialog = false },
            containerColor = SurfaceDark,
            title = {
                Text("Gerar Cultos Fixos", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Crie automaticamente todos os cultos oficiais da igreja do mês escolhido (Quartas às 20h, Sábados às 9h, JA às 17h, Domingos às 19h), respeitando os parâmetros de sonoplastas necessários.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedOffset == 0) PrimaryIndigoDark.copy(alpha = 0.3f) else androidx.compose.ui.graphics.Color.Transparent)
                            .border(1.dp, if (selectedOffset == 0) PrimaryIndigoLight else BorderMedium, RoundedCornerShape(12.dp))
                            .clickable { selectedOffset = 0 }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Mês Atual ($currentMonthName)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        RadioButton(selected = selectedOffset == 0, onClick = { selectedOffset = 0 }, colors = RadioButtonDefaults.colors(selectedColor = PrimaryIndigoLight))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedOffset == 1) PrimaryIndigoDark.copy(alpha = 0.3f) else androidx.compose.ui.graphics.Color.Transparent)
                            .border(1.dp, if (selectedOffset == 1) PrimaryIndigoLight else BorderMedium, RoundedCornerShape(12.dp))
                            .clickable { selectedOffset = 1 }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Próximo Mês ($nextMonthName)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        RadioButton(selected = selectedOffset == 1, onClick = { selectedOffset = 1 }, colors = RadioButtonDefaults.colors(selectedColor = PrimaryIndigoLight))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.generateMonthlyServices(selectedOffset)
                        showGenerateMonthDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Text("Gerar Cultos", color = TextPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGenerateMonthDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }
}

data class UserSuggestion(
    val user: User,
    val isAvailable: Boolean,
    val hasConflict: Boolean,
    val scaleCount: Int
)

@Composable
fun SuggestionItemCard(
    suggestion: UserSuggestion,
    onSelect: () -> Unit
) {
    val borderColor = if (suggestion.hasConflict) {
        RoseError.copy(alpha = 0.3f)
    } else if (suggestion.isAvailable) {
        EmeraldSuccess.copy(alpha = 0.5f)
    } else {
        BorderSubtle
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = !suggestion.hasConflict) { onSelect() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (suggestion.isAvailable && !suggestion.hasConflict) EmeraldSuccess.copy(alpha = 0.1f)
                        else if (suggestion.hasConflict) RoseError.copy(alpha = 0.1f)
                        else TextTertiary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = suggestion.user.name.take(1).uppercase(),
                    color = if (suggestion.isAvailable && !suggestion.hasConflict) EmeraldSuccess else if (suggestion.hasConflict) RoseError else TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = suggestion.user.name,
                    color = if (suggestion.hasConflict) TextTertiary else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${suggestion.user.level} • ${suggestion.scaleCount} escala(s) recente(s)",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Badge Status Indicator
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (suggestion.hasConflict) RoseError.copy(alpha = 0.1f)
                    else if (suggestion.isAvailable) EmeraldSuccess.copy(alpha = 0.1f)
                    else TextTertiary.copy(alpha = 0.15f)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (suggestion.hasConflict) "CONFLITO DE HORÁRIO" else if (suggestion.isAvailable) "DISPONÍVEL" else "SEM REGISTRO",
                color = if (suggestion.hasConflict) RoseError else if (suggestion.isAvailable) EmeraldSuccess else TextSecondary,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ScheduledOperatorCard(
    user: User,
    schedule: Schedule,
    onRemove: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    var expandedStatus by remember { mutableStateOf(false) }
    val statuses = listOf("ESCALADO", "CONFIRMADO", "RECUSADO", "CONCLUIDO")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryIndigoLight.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(2).uppercase(),
                        color = PrimaryIndigoLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = user.name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Clickable status selector dropdown
                        Box {
                            Row(
                                modifier = Modifier
                                    .clickable { expandedStatus = true }
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (schedule.status) {
                                            "CONFIRMADO" -> EmeraldSuccess.copy(alpha = 0.15f)
                                            "RECUSADO" -> RoseError.copy(alpha = 0.15f)
                                            else -> AmberWarning.copy(alpha = 0.15f)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = schedule.status,
                                    color = when (schedule.status) {
                                        "CONFIRMADO" -> EmeraldSuccess
                                        "RECUSADO" -> RoseError
                                        else -> AmberWarning
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = expandedStatus,
                                onDismissRequest = { expandedStatus = false },
                                modifier = Modifier.background(SurfaceDark)
                            ) {
                                statuses.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text(s, color = TextPrimary, fontSize = 12.sp) },
                                        onClick = {
                                            onStatusChange(s)
                                            expandedStatus = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = user.level.uppercase(),
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(RoseError.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover",
                    tint = RoseError,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptySlotCard(
    index: Int,
    onAdd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark.copy(alpha = 0.4f))
            .border(
                width = 1.dp,
                color = BorderSubtle,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onAdd() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, BorderSubtle, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = index.toString(),
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Vaga de Sonoplasta disponível",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Clique para escalar voluntário",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar",
                tint = PrimaryIndigoLight,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
