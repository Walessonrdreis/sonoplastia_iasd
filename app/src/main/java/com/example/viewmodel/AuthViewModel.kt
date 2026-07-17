package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.User
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AppRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Basic mock initialization for admin and volunteer
    init {
        viewModelScope.launch {
            val existing = repository.getUserByEmail("admin@admin.com")
            if (existing == null) {
                repository.insertUser(
                    User(
                        name = "Administrador",
                        email = "admin@admin.com",
                        phone = "000000000",
                        passwordHash = "admin", // Demo purpose
                        role = "ADMIN",
                        status = "ACTIVE"
                    )
                )
            }
            val existingVolunteer = repository.getUserByEmail("voluntario@sonoplastia.com")
            if (existingVolunteer == null) {
                repository.insertUser(
                    User(
                        name = "Gabriel Silva",
                        email = "voluntario@sonoplastia.com",
                        phone = "11999998888",
                        passwordHash = "voluntario", // Demo purpose
                        role = "VOLUNTEER",
                        status = "ACTIVE"
                    )
                )
            }
        }
    }

    fun login(email: String, pass: String, onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user != null && user.passwordHash == pass) {
                _currentUser.value = user
                _error.value = null
                onSuccess(user)
            } else {
                _error.value = "Credenciais inválidas"
            }
        }
    }
    
    fun register(name: String, email: String, phone: String, pass: String, confirm: String, onSuccess: () -> Unit) {
        if (pass != confirm) {
            _error.value = "As senhas não coincidem"
            return
        }
        viewModelScope.launch {
            val existing = repository.getUserByEmail(email)
            if (existing != null) {
                _error.value = "Email já cadastrado"
                return@launch
            }
            val newUser = User(
                name = name,
                email = email,
                phone = phone,
                passwordHash = pass,
                role = "VOLUNTEER",
                status = "ACTIVE"
            )
            repository.insertUser(newUser)
            _error.value = null
            onSuccess()
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun logout() {
        _currentUser.value = null
    }
}
