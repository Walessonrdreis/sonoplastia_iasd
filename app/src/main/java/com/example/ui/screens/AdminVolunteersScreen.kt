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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.User
import com.example.viewmodel.HomeViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVolunteersScreen(viewModel: HomeViewModel, onBack: () -> Unit) {
    val users by viewModel.allUsers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredUsers = remember(users, searchQuery) {
        users.filter { 
            it.name.contains(searchQuery, ignoreCase = true) || 
            it.email.contains(searchQuery, ignoreCase = true) ||
            it.phone.contains(searchQuery, ignoreCase = true)
        }
    }

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
                        text = "Voluntários",
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
            // Search Input
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryIndigoLight,
                unfocusedBorderColor = BorderMedium,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PrimaryIndigoLight,
                focusedLabelColor = PrimaryIndigoLight,
                unfocusedLabelColor = TextSecondary
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar voluntário...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = textFieldColors,
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredUsers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhum voluntário encontrado",
                            color = TextSecondary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredUsers, key = { it.id }) { user ->
                        VolunteerCard(
                            user = user,
                            onToggleStatus = { viewModel.toggleUserStatus(user) },
                            onLevelChange = { level -> viewModel.updateUserLevel(user, level) },
                            onRoleChange = { role -> viewModel.updateUserRole(user, role) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun VolunteerCard(
    user: User,
    onToggleStatus: () -> Unit,
    onLevelChange: (String) -> Unit,
    onRoleChange: (String) -> Unit
) {
    var expandedLevel by remember { mutableStateOf(false) }
    var expandedRole by remember { mutableStateOf(false) }

    val levels = listOf("Iniciante", "Intermediário", "Avançado", "Líder")
    val roles = listOf("VOLUNTEER", "ADMIN")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(PrimaryIndigo.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(2).uppercase(),
                            color = PrimaryIndigoLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = user.name,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clickable { expandedRole = true }
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PrimaryIndigoLight.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (user.role == "ADMIN") "ADMINISTRADOR" else "VOLUNTÁRIO",
                                        color = PrimaryIndigoLight,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = PrimaryIndigoLight,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = expandedRole,
                                    onDismissRequest = { expandedRole = false },
                                    modifier = Modifier.background(SurfaceDark)
                                ) {
                                    roles.forEach { r ->
                                        DropdownMenuItem(
                                            text = { 
                                                Text(
                                                    text = if (r == "ADMIN") "ADMINISTRADOR" else "VOLUNTÁRIO", 
                                                    color = TextPrimary,
                                                    fontSize = 14.sp
                                                ) 
                                            },
                                            onClick = {
                                                onRoleChange(r)
                                                expandedRole = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            // Level Badge Dropdown
                            Box(
                                modifier = Modifier
                                    .clickable { expandedLevel = true }
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TextTertiary.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.level.uppercase(),
                                        color = TextSecondary,
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
                                    expanded = expandedLevel,
                                    onDismissRequest = { expandedLevel = false },
                                    modifier = Modifier.background(SurfaceDark)
                                ) {
                                    levels.forEach { lvl ->
                                        DropdownMenuItem(
                                            text = { Text(text = lvl, color = TextPrimary, fontSize = 14.sp) },
                                            onClick = {
                                                onLevelChange(lvl)
                                                expandedLevel = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Switch for Active/Inactive
                Switch(
                    checked = user.status == "ACTIVE",
                    onCheckedChange = { onToggleStatus() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = EmeraldSuccess,
                        checkedTrackColor = EmeraldSuccess.copy(alpha = 0.3f),
                        uncheckedThumbColor = RoseError,
                        uncheckedTrackColor = RoseError.copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = BorderSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Contact Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "E-mail",
                        tint = TextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = user.email,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Telefone",
                        tint = TextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = user.phone,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
