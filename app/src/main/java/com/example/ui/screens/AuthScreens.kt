package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.navigation.Route
import com.example.viewmodel.AuthViewModel
import com.example.ui.theme.*
import com.example.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: AuthViewModel, onLoginSuccess: (Int, String) -> Unit, onNavigateToRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp),
            tint = PrimaryIndigoLight
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "MINISTÉRIO DE SONOPLASTIA",
            color = PrimaryIndigoLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Text(
            text = "Sonoplastia", 
            color = TextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-1).sp
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
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
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = textFieldColors,
            shape = RoundedCornerShape(16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; viewModel.clearError() },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = textFieldColors,
            shape = RoundedCornerShape(16.dp)
        )
        
        if (error != null) {
            Text(text = error!!, color = RoseError, modifier = Modifier.padding(top = 16.dp), fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = { viewModel.login(email, password) { user -> onLoginSuccess(user.id, user.role) } },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = PrimaryIndigoDark),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigoDark),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Entrar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }

        val isDevMode = try {
            BuildConfig.DESENVOLVIMENTO == "true"
        } catch (e: Exception) {
            false
        }

        if (isDevMode) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { 
                        viewModel.login("admin@admin.com", "admin") { user -> 
                            onLoginSuccess(user.id, user.role) 
                        } 
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(56.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryIndigoLight.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryIndigoLight),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Admin (Dev)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { 
                        viewModel.login("voluntario@sonoplastia.com", "voluntario") { user -> 
                            onLoginSuccess(user.id, user.role) 
                        } 
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(56.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldSuccess),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Voluntário (Dev)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onNavigateToRegister) {
            Text("Criar Conta", color = TextSecondary, fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: AuthViewModel, onRegisterSuccess: () -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    val error by viewModel.error.collectAsState()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryIndigoLight,
        unfocusedBorderColor = BorderMedium,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        cursorColor = PrimaryIndigoLight,
        focusedLabelColor = PrimaryIndigoLight,
        unfocusedLabelColor = TextSecondary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Criar Conta", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome completo") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors, shape = RoundedCornerShape(16.dp))
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), colors = textFieldColors, shape = RoundedCornerShape(16.dp))
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefone") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), colors = textFieldColors, shape = RoundedCornerShape(16.dp))
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), colors = textFieldColors, shape = RoundedCornerShape(16.dp))
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = confirm, onValueChange = { confirm = it }, label = { Text("Confirmar Senha") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), colors = textFieldColors, shape = RoundedCornerShape(16.dp))
        
        if (error != null) {
            Text(text = error!!, color = RoseError, modifier = Modifier.padding(top = 16.dp), fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { viewModel.register(name, email, phone, password, confirm) { onRegisterSuccess() } },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = PrimaryIndigoDark),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigoDark),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Cadastrar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onBack) {
            Text("Voltar", color = TextSecondary, fontSize = 14.sp)
        }
    }
}

