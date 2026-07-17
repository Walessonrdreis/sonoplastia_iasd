package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.Schedule
import com.example.viewmodel.HomeViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel, 
    onNavigateToAvailability: () -> Unit,
    onLogout: () -> Unit,
    onBackToAdmin: () -> Unit
) {
    val mySchedules by viewModel.mySchedules.collectAsState()
    val events by viewModel.events.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val allSchedules by viewModel.allSchedules.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val userIdState by viewModel.userId.collectAsState()
    val currentUserId = userIdState ?: 0

    val currentUser = allUsers.find { it.id == currentUserId }
    val isUserAdmin = currentUser?.role == "ADMIN"

    var currentTab by remember { mutableStateOf(0) } // 0 = Início, 1 = Agenda, 2 = Avisos

    var showSubDialog by remember { mutableStateOf(false) }
    var subReason by remember { mutableStateOf("") }
    var subScheduleToRequest by remember { mutableStateOf<Schedule?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = SurfaceDark,
                drawerContentColor = TextPrimary,
                modifier = Modifier
                    .width(310.dp)
                    .fillMaxHeight()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                    .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PrimaryIndigo.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = currentUser?.name?.take(2)?.uppercase() ?: "SO"
                            Text(
                                text = initials,
                                color = PrimaryIndigoLight,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = currentUser?.name ?: "Voluntário",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isUserAdmin) "ADMIN (MODO ESPELHO)" else "OPERADOR ${currentUser?.level?.uppercase() ?: "VOLUNTÁRIO"}",
                                color = if (isUserAdmin) AmberWarning else PrimaryIndigoLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderSubtle)

                // Navigation Items
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "NAVEGAÇÃO DO OPERADOR",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Painel Geral item
                    NavigationDrawerItem(
                        label = { Text("Painel Geral", fontWeight = FontWeight.Medium) },
                        selected = currentTab == 0,
                        onClick = {
                            currentTab = 0
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PrimaryIndigoDark.copy(alpha = 0.3f),
                            selectedIconColor = PrimaryIndigoLight,
                            selectedTextColor = PrimaryIndigoLight,
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Agenda do Mês item
                    NavigationDrawerItem(
                        label = { Text("Agenda do Mês / Auto-Seleção", fontWeight = FontWeight.Medium) },
                        selected = currentTab == 1,
                        onClick = {
                            currentTab = 1
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PrimaryIndigoDark.copy(alpha = 0.3f),
                            selectedIconColor = PrimaryIndigoLight,
                            selectedTextColor = PrimaryIndigoLight,
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Avisos item
                    NavigationDrawerItem(
                        label = { Text("Comunicados e Avisos", fontWeight = FontWeight.Medium) },
                        selected = currentTab == 2,
                        onClick = {
                            currentTab = 2
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                        badge = {
                            val unreadCount = notifications.count { !it.isRead }
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(RoseError)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(unreadCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PrimaryIndigoDark.copy(alpha = 0.3f),
                            selectedIconColor = PrimaryIndigoLight,
                            selectedTextColor = PrimaryIndigoLight,
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderSubtle)

                    Text(
                        text = "GERENCIAMENTO",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Indicar Disponibilidade
                    NavigationDrawerItem(
                        label = { Text("Indicar Disponibilidade", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateToAvailability()
                        },
                        icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (isUserAdmin) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderSubtle)

                        Text(
                            text = "VISÃO ADMINISTRADOR",
                            color = TextTertiary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        // Voltar ao Painel Admin
                        NavigationDrawerItem(
                            label = { Text("Voltar ao Painel Admin", fontWeight = FontWeight.Bold, color = AmberWarning) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onBackToAdmin()
                            },
                            icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = AmberWarning) },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Sair item
                    NavigationDrawerItem(
                        label = { Text("Sair da Conta", fontWeight = FontWeight.Bold, color = RoseError) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onLogout()
                        },
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = RoseError) },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            containerColor = BgDark,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 40.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Hamburguer Menu Button
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .border(1.dp, BorderSubtle, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Abrir Menu",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "MINISTÉRIO DE SONOPLASTIA",
                            color = PrimaryIndigoLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = when(currentTab) {
                                0 -> "Painel Geral"
                                1 -> "Agenda do Mês"
                                2 -> "Comunicados"
                                else -> "Sonoplastia"
                            },
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                    }
                }
            }
        ) { padding ->
        if (showSubDialog && subScheduleToRequest != null) {
            AlertDialog(
                onDismissRequest = { showSubDialog = false },
                title = { Text("Solicitar Substituição", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Informe aos administradores o motivo pelo qual você precisa de um substituto para esta escala.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        OutlinedTextField(
                            value = subReason,
                            onValueChange = { subReason = it },
                            placeholder = { Text("Ex: Viagem de trabalho / Motivos de saúde") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryIndigoLight,
                                unfocusedBorderColor = BorderMedium,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedLabelColor = PrimaryIndigoLight,
                                unfocusedLabelColor = TextTertiary,
                                cursorColor = PrimaryIndigoLight
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (subReason.isNotEmpty()) {
                                viewModel.requestSubstitution(subScheduleToRequest!!, subReason)
                                showSubDialog = false
                                subReason = ""
                                subScheduleToRequest = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigoDark),
                        shape = RoundedCornerShape(12.dp),
                        enabled = subReason.isNotEmpty()
                    ) {
                        Text("Enviar Pedido", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSubDialog = false; subScheduleToRequest = null }) {
                        Text("Cancelar", color = TextTertiary, fontWeight = FontWeight.Medium)
                    }
                },
                containerColor = SurfaceDark,
                shape = RoundedCornerShape(24.dp)
            )
        }

        when (currentTab) {
            0 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        val upcoming = mySchedules
                            .filter { it.status == "ESCALADO" || it.status == "CONFIRMADO" || it.status == "SUBSTITUICAO_SOLICITADA" }
                            .mapNotNull { sched ->
                                val ev = events.find { it.id == sched.eventId }
                                if (ev != null) sched to ev else null
                            }
                            .sortedBy { it.second.eventDate }
                            .firstOrNull()

                        NextScheduleCard(
                            schedule = upcoming?.first,
                            eventName = upcoming?.second?.title ?: "Nenhuma Escala",
                            eventDate = upcoming?.second?.let { "${formatDate(it.eventDate)} • ${it.startTime} - ${it.endTime}" } ?: "Livre",
                            onConfirm = { sched -> viewModel.updateScheduleStatus(sched, "CONFIRMADO") },
                            onReject = { sched -> viewModel.updateScheduleStatus(sched, "RECUSADO") },
                            onRequestSubstitution = { sched ->
                                subScheduleToRequest = sched
                                showSubDialog = true
                            }
                        )
                    }

                    item {
                        AvailabilitySection(onNavigateToAvailability)
                    }

                    item {
                        QuickStatsSection(
                            monthlySchedules = mySchedules.size,
                            confirmationRate = 100
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
            1 -> {
                val sortedEvents = events.sortedBy { it.eventDate }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "SELECIONE OS DIAS QUE IRÁ AJUDAR",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (sortedEvents.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Nenhum culto gerado neste mês.", color = TextTertiary, fontSize = 14.sp)
                            }
                        }
                    } else {
                        items(sortedEvents.size) { index ->
                            val event = sortedEvents[index]
                            val eventSchedules = allSchedules.filter { it.eventId == event.id }
                            val isUserScheduled = eventSchedules.any { it.userId == currentUserId }
                            val myScheduleForEvent = eventSchedules.find { it.userId == currentUserId }
                            
                            val sdf = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("pt", "BR"))
                            val dateStr = sdf.format(Date(event.eventDate)).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SurfaceDark)
                                    .border(
                                        width = 1.dp,
                                        color = if (isUserScheduled) EmeraldSuccess.copy(alpha = 0.5f) else BorderSubtle,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = event.title,
                                                color = if (isUserScheduled) EmeraldSuccess else TextPrimary,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "$dateStr • ${event.startTime} - ${event.endTime}",
                                                color = TextSecondary,
                                                fontSize = 12.sp
                                            )
                                            if (event.description.isNotEmpty()) {
                                                Text(
                                                    text = event.description,
                                                    color = TextTertiary,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (isUserScheduled) EmeraldSuccess.copy(alpha = 0.15f)
                                                    else if (eventSchedules.size >= event.requiredOperators) BorderMedium
                                                    else PrimaryIndigoLight.copy(alpha = 0.15f)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (isUserScheduled) "ESCALADO"
                                                       else if (eventSchedules.size >= event.requiredOperators) "COMPLETO"
                                                       else "VAGAS (${event.requiredOperators - eventSchedules.size})",
                                                color = if (isUserScheduled) EmeraldSuccess
                                                        else if (eventSchedules.size >= event.requiredOperators) TextTertiary
                                                        else PrimaryIndigoLight,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    if (eventSchedules.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = TextTertiary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "Equipe: " + eventSchedules.mapNotNull { sched ->
                                                    allUsers.find { it.id == sched.userId }?.name
                                                }.joinToString(", "),
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    if (isUserScheduled && myScheduleForEvent != null) {
                                        OutlinedButton(
                                            onClick = { viewModel.withdrawFromEvent(event.id, currentUserId) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(40.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, RoseError.copy(alpha = 0.5f)),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Sair desta Escala", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    } else if (eventSchedules.size < event.requiredOperators) {
                                        Button(
                                            onClick = { viewModel.signUpForEvent(event.id, currentUserId) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(40.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Candidatar-se para este Culto", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
            2 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "AVISOS E COMUNICADOS",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (notifications.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Nenhum aviso no momento.", color = TextTertiary, fontSize = 14.sp)
                            }
                        }
                    } else {
                        items(notifications.size) { index ->
                            val notification = notifications[index]
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SurfaceDark)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                                    .padding(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notification.title,
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (!notification.isRead) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(RoseError)
                                            )
                                        }
                                    }
                                    Text(
                                        text = notification.message,
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = formatDate(notification.createdAt),
                                        color = TextTertiary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun NextScheduleCard(
    schedule: Schedule?,
    eventName: String,
    eventDate: String,
    onConfirm: (Schedule) -> Unit,
    onReject: (Schedule) -> Unit,
    onRequestSubstitution: (Schedule) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderSubtle, RoundedCornerShape(28.dp))
            .padding(24.dp)
    ) {
        if (schedule?.status == "ESCALADO" || schedule?.status == "CONFIRMADO" || schedule?.status == "SUBSTITUICAO_SOLICITADA") {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(
                        when (schedule.status) {
                            "CONFIRMADO" -> EmeraldSuccess.copy(alpha = 0.15f)
                            "SUBSTITUICAO_SOLICITADA" -> RoseError.copy(alpha = 0.15f)
                            else -> AmberWarning.copy(alpha = 0.1f)
                        }
                    )
                    .border(
                        1.dp,
                        when (schedule.status) {
                            "CONFIRMADO" -> EmeraldSuccess.copy(alpha = 0.3f)
                            "SUBSTITUICAO_SOLICITADA" -> RoseError.copy(alpha = 0.3f)
                            else -> AmberWarning.copy(alpha = 0.2f)
                        },
                        CircleShape
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when (schedule.status) {
                        "SUBSTITUICAO_SOLICITADA" -> "TROCA SOLICITADA"
                        else -> schedule.status.uppercase()
                    },
                    color = when (schedule.status) {
                        "CONFIRMADO" -> EmeraldSuccess
                        "SUBSTITUICAO_SOLICITADA" -> RoseError
                        else -> AmberWarning
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Column {
            Text(
                text = "PRÓXIMA ESCALA",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PrimaryIndigo.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (schedule?.status == "CONFIRMADO") Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (schedule?.status == "CONFIRMADO") EmeraldSuccess else PrimaryIndigoLight,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = eventName,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = eventDate,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (schedule?.status == "ESCALADO") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { onConfirm(schedule) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = PrimaryIndigoDark),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigoDark),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Confirmar", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = { onReject(schedule) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary.copy(alpha = 0.8f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderMedium),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Recusar", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else if (schedule?.status == "CONFIRMADO") {
                OutlinedButton(
                    onClick = { onRequestSubstitution(schedule) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoseError.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Solicitar Substituição", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun AvailabilitySection(onNavigate: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SUA DISPONIBILIDADE",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Text(
                text = "EDITAR",
                color = PrimaryIndigoLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onNavigate() }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val days = listOf("D", "S", "T", "Q", "Q", "S", "S")
            val availableDays = listOf(0, 3, 6) // Example: Sun, Wed, Sat
            days.forEachIndexed { index, day ->
                val isAvailable = availableDays.contains(index)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark.copy(alpha = if (isAvailable) 1f else 0.4f))
                        .border(
                            1.dp,
                            if (isAvailable) PrimaryIndigo.copy(alpha = 0.2f) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = day,
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isAvailable) PrimaryIndigo else TextTertiary)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickStatsSection(monthlySchedules: Int, confirmationRate: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = monthlySchedules.toString(),
                    color = PrimaryIndigoLight,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ESCALAS MÊS",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }
        
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "${confirmationRate}%",
                    color = EmeraldSuccess,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "CONFIRMAÇÃO",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun BottomNavIcon(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, badge: Int = 0, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (selected) PrimaryIndigoDark.copy(alpha = 0.3f) else Color.Transparent)
                .padding(horizontal = 20.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (selected) PrimaryIndigoLight else TextTertiary,
                modifier = Modifier.size(24.dp)
            )
            if (badge > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-4).dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(RoseError)
                        .border(2.dp, SurfaceVariantDark, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = if (selected) PrimaryIndigoLight else TextTertiary,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd 'de' MMM", Locale("pt", "BR"))
    return sdf.format(Date(timestamp))
}

