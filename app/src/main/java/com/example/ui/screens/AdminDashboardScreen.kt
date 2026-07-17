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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.HomeViewModel
import com.example.ui.theme.*
import com.example.data.local.entity.SubstitutionRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AdminScreen {
    Dashboard,
    Substitutions,
    Announcements
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: HomeViewModel, 
    onNavigateToVolunteers: () -> Unit,
    onNavigateToCreateSchedule: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToUserPortal: (Int) -> Unit
) {
    val users by viewModel.allUsers.collectAsState()
    val schedules by viewModel.allSchedules.collectAsState()
    val events by viewModel.events.collectAsState()
    val substitutionRequests by viewModel.allSubstitutionRequests.collectAsState()
    val userIdState by viewModel.userId.collectAsState()
    val currentUserId = userIdState ?: 1

    var currentScreen by remember { mutableStateOf(AdminScreen.Dashboard) }

    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Form states for announcements
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }
    var showBroadcastSuccess by remember { mutableStateOf(false) }

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
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryIndigo.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = PrimaryIndigoLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Sonoplastia",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "PAINEL DE CONTROLE",
                                color = PrimaryIndigoLight,
                                fontSize = 10.sp,
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
                        text = "GERENCIAMENTO PRINCIPAL",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Dashboard screen item
                    NavigationDrawerItem(
                        label = { Text("Painel Geral", fontWeight = FontWeight.Medium) },
                        selected = currentScreen == AdminScreen.Dashboard,
                        onClick = {
                            currentScreen = AdminScreen.Dashboard
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

                    // Substitution screen item
                    NavigationDrawerItem(
                        label = { Text("Solicitações de Troca", fontWeight = FontWeight.Medium) },
                        selected = currentScreen == AdminScreen.Substitutions,
                        onClick = {
                            currentScreen = AdminScreen.Substitutions
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                        badge = {
                            val pendingCount = substitutionRequests.count { it.status == "PENDING" }
                            if (pendingCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(RoseError)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(pendingCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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

                    // Announcements screen item
                    NavigationDrawerItem(
                        label = { Text("Central de Comunicados", fontWeight = FontWeight.Medium) },
                        selected = currentScreen == AdminScreen.Announcements,
                        onClick = {
                            currentScreen = AdminScreen.Announcements
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
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
                        text = "AÇÕES RÁPIDAS",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Create Schedule shortcut
                    NavigationDrawerItem(
                        label = { Text("Criar e Gerar Escalas", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateToCreateSchedule()
                        },
                        icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Volunteers shortcut
                    NavigationDrawerItem(
                        label = { Text("Gerenciar Voluntários", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateToVolunteers()
                        },
                        icon = { Icon(Icons.Default.People, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Portal view
                    NavigationDrawerItem(
                        label = { Text("Ver como Voluntário", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateToUserPortal(currentUserId)
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Logout item
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
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
                                text = "SISTEMA SONOPLASTIA",
                                color = PrimaryIndigoLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when(currentScreen) {
                                    AdminScreen.Dashboard -> "Painel Geral"
                                    AdminScreen.Substitutions -> "Substituições"
                                    AdminScreen.Announcements -> "Comunicados"
                                },
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                        }
                    }

                    // Header quick badge for pending substitutions
                    val pendingCount = substitutionRequests.count { it.status == "PENDING" }
                    if (pendingCount > 0 && currentScreen != AdminScreen.Substitutions) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(RoseError.copy(alpha = 0.15f))
                                .border(1.dp, RoseError.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .clickable { currentScreen = AdminScreen.Substitutions }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(RoseError)
                                )
                                Text(
                                    text = "$pendingCount Trocas",
                                    color = RoseError,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                when (currentScreen) {
                    AdminScreen.Dashboard -> {
                        // MAIN DASHBOARD SCREEN
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                // Row 1 of metrics
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onNavigateToVolunteers() }
                                    ) {
                                        AdminStatCard(title = "VOLUNTÁRIOS", value = users.size.toString(), color = PrimaryIndigoLight)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onNavigateToCreateSchedule() }
                                    ) {
                                        AdminStatCard(title = "EVENTOS", value = events.size.toString(), color = EmeraldSuccess)
                                    }
                                }
                            }

                            item {
                                // Row 2 of metrics
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    val pending = schedules.count { it.status == "ESCALADO" }
                                    val subs = substitutionRequests.count { it.status == "PENDING" }
                                    AdminStatCard(title = "PENDENTES ESCALA", value = pending.toString(), color = AmberWarning, modifier = Modifier.weight(1f))
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { currentScreen = AdminScreen.Substitutions }
                                    ) {
                                        AdminStatCard(title = "PEDIDOS TROCA", value = subs.toString(), color = RoseError)
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ROTA DE ADMINISTRAÇÃO",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Large Visual Action Cards (Traditional Navigation UX)
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    // Create / Manage schedules
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(SurfaceDark)
                                            .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                                            .clickable { onNavigateToCreateSchedule() }
                                            .padding(20.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(PrimaryIndigo.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.DateRange, contentDescription = null, tint = PrimaryIndigoLight, modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Criar e Gerar Escalas", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                            Text("Geração mensal de cultos e auto-sugestão", color = TextTertiary, fontSize = 12.sp)
                                        }
                                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = TextTertiary)
                                    }

                                    // Manage volunteers database
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(SurfaceDark)
                                            .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                                            .clickable { onNavigateToVolunteers() }
                                            .padding(20.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(EmeraldSuccess.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.People, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Gerenciar Voluntários", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                            Text("Aprovação, nível técnico e status ativo", color = TextTertiary, fontSize = 12.sp)
                                        }
                                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = TextTertiary)
                                    }
                                }
                            }
                        }
                    }
                    AdminScreen.Substitutions -> {
                        // SUBSTITUTION REQUESTS SCREEN
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Text(
                                    text = "SOLICITAÇÕES DE SUBSTITUIÇÃO PENDENTES",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            val pendingRequests = substitutionRequests.filter { it.status == "PENDING" }
                            val historyRequests = substitutionRequests.filter { it.status != "PENDING" }

                            if (pendingRequests.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(SurfaceDark)
                                            .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text("Tudo sob controle!", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                            Text("Nenhum pedido de substituição pendente.", color = TextTertiary, fontSize = 12.sp)
                                        }
                                    }
                                }
                            } else {
                                items(pendingRequests) { request ->
                                    val requester = users.find { it.id == request.requesterId }
                                    val schedule = schedules.find { it.id == request.scheduleId }
                                    val event = schedule?.let { s -> events.find { it.id == s.eventId } }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(SurfaceDark)
                                            .border(1.dp, RoseError.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
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
                                                        text = requester?.name ?: "Voluntário",
                                                        color = TextPrimary,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        text = event?.title ?: "Culto não encontrado",
                                                        color = PrimaryIndigoLight,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    if (event != null) {
                                                        val sdf = SimpleDateFormat("EEEE, dd/MM", Locale("pt", "BR"))
                                                        val dateStr = sdf.format(Date(event.eventDate))
                                                        Text(
                                                            text = "$dateStr • ${event.startTime} - ${event.endTime}",
                                                            color = TextSecondary,
                                                            fontSize = 12.sp
                                                        )
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(RoseError.copy(alpha = 0.15f))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = "SOLICITADA",
                                                        color = RoseError,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(BgDark)
                                                    .padding(12.dp)
                                            ) {
                                                Column {
                                                    Text("Motivo apresentado:", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = "\"${request.reason}\"",
                                                        color = TextSecondary,
                                                        fontSize = 13.sp
                                                    )
                                                }
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Button(
                                                    onClick = { viewModel.approveSubstitutionRequest(request) },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(44.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Aprovar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                }

                                                OutlinedButton(
                                                    onClick = { viewModel.rejectSubstitutionRequest(request) },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(44.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, RoseError.copy(alpha = 0.5f)),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Recusar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (historyRequests.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "HISTÓRICO RECENTE",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                items(historyRequests.take(10)) { request ->
                                    val requester = users.find { it.id == request.requesterId }
                                    val schedule = schedules.find { it.id == request.scheduleId }
                                    val event = schedule?.let { s -> events.find { it.id == s.eventId } }

                                    val isApproved = request.status == "APPROVED"

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(SurfaceDark)
                                            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = requester?.name ?: "Voluntário",
                                                    color = TextPrimary,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = event?.title ?: "Culto liberado",
                                                    color = TextSecondary,
                                                    fontSize = 12.sp
                                                )
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        if (isApproved) EmeraldSuccess.copy(alpha = 0.1f)
                                                        else RoseError.copy(alpha = 0.1f)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = if (isApproved) "APROVADA" else "RECUSADA",
                                                    color = if (isApproved) EmeraldSuccess else RoseError,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    AdminScreen.Announcements -> {
                        // BROADCAST ANNOUNCEMENTS SCREEN
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Text(
                                    text = "ENVIAR AVISO PARA A EQUIPE",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(SurfaceDark)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                                        .padding(20.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        OutlinedTextField(
                                            value = broadcastTitle,
                                            onValueChange = { broadcastTitle = it },
                                            label = { Text("Título do Aviso") },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = PrimaryIndigoLight,
                                                unfocusedBorderColor = BorderMedium,
                                                focusedLabelColor = PrimaryIndigoLight,
                                                unfocusedLabelColor = TextTertiary,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                cursorColor = PrimaryIndigoLight
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        OutlinedTextField(
                                            value = broadcastMessage,
                                            onValueChange = { broadcastMessage = it },
                                            label = { Text("Mensagem") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = PrimaryIndigoLight,
                                                unfocusedBorderColor = BorderMedium,
                                                focusedLabelColor = PrimaryIndigoLight,
                                                unfocusedLabelColor = TextTertiary,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                cursorColor = PrimaryIndigoLight
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = false
                                        )

                                        Button(
                                            onClick = {
                                                if (broadcastTitle.isNotEmpty() && broadcastMessage.isNotEmpty()) {
                                                    viewModel.sendAnnouncementToAll(broadcastTitle, broadcastMessage)
                                                    broadcastTitle = ""
                                                    broadcastMessage = ""
                                                    showBroadcastSuccess = true
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = PrimaryIndigoDark),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigoDark),
                                            shape = RoundedCornerShape(12.dp),
                                            enabled = broadcastTitle.isNotEmpty() && broadcastMessage.isNotEmpty()
                                        ) {
                                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Disparar Comunicado", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            if (showBroadcastSuccess) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(EmeraldSuccess.copy(alpha = 0.15f))
                                            .border(1.dp, EmeraldSuccess.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                            .padding(16.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text("Aviso enviado com sucesso para todos os voluntários!", color = EmeraldSuccess, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                            Spacer(modifier = Modifier.weight(1f))
                                            IconButton(onClick = { showBroadcastSuccess = false }) {
                                                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "DICAS DE COMUNICAÇÃO",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(SurfaceDark)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                                        .padding(16.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("📢 Alinhamento e Reuniões", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("Utilize para avisar sobre escalas especiais, ensaios gerais, troca de equipamentos no templo ou reuniões administrativas de sonoplastia.", color = TextSecondary, fontSize = 12.sp)
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("⚡ Notificações Instantâneas", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("O aviso aparecerá imediatamente na aba 'Avisos' no aplicativo de cada operador ativo do ministério.", color = TextSecondary, fontSize = 12.sp)
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
fun AdminStatCard(title: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = value,
                color = color,
                fontSize = 28.sp,
                fontWeight = FontWeight.Light
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }
    }
}
