package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigation.Route
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminVolunteersScreen
import com.example.ui.screens.AdminCreateScheduleScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppViewModelFactory
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigation()
        }
      }
    }
  }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = AppViewModelFactory())
    
    NavHost(navController = navController, startDestination = Route.Login.route) {
        composable(Route.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { userId, role ->
                    if (role == "ADMIN") {
                        navController.navigate(Route.AdminDashboard.route) {
                            popUpTo(Route.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Route.Home.route + "/$userId") {
                            popUpTo(Route.Login.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Route.Register.route)
                }
            )
        }
        
        composable(Route.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Route.Home.route + "/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
            val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelFactory())
            if (userId != null) {
                homeViewModel.setUserId(userId)
            }
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToAvailability = {
                    navController.navigate(Route.Availability.route)
                },
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                },
                onBackToAdmin = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Route.AdminDashboard.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelFactory())
            AdminDashboardScreen(
                viewModel = homeViewModel,
                onNavigateToVolunteers = {
                    navController.navigate(Route.AdminVolunteers.route)
                },
                onNavigateToCreateSchedule = {
                    navController.navigate(Route.CreateSchedule.route)
                },
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                },
                onNavigateToUserPortal = { userId ->
                    navController.navigate(Route.Home.route + "/$userId")
                }
            )
        }

        composable(Route.CreateSchedule.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelFactory())
            AdminCreateScheduleScreen(
                viewModel = homeViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Route.AdminVolunteers.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelFactory())
            AdminVolunteersScreen(
                viewModel = homeViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Route.Availability.route) {
            // Placeholder
        }
    }
}
