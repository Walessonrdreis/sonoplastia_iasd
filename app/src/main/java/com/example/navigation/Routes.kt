package com.example.navigation

sealed class Route(val route: String) {
    object Splash : Route("splash")
    object Login : Route("login")
    object Register : Route("register")
    object Home : Route("home")
    object Availability : Route("availability")
    object Calendar : Route("calendar")
    object Profile : Route("profile")
    object AdminDashboard : Route("admin_dashboard")
    object AdminVolunteers : Route("admin_volunteers")
    object CreateSchedule : Route("create_schedule")
}
