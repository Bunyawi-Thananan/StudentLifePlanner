package com.studentlifeplanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studentlifeplanner.ui.screen.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Courses : Screen("courses")
    object Assignments : Screen("assignments")
    object Exams : Screen("exams")
    object Schedule : Screen("schedule")
    object Notes : Screen("notes")
    object Profile : Screen("profile")
}

@Composable
fun AppNavigation(
    isLoggedIn: Boolean,
    navController: NavHostController = rememberNavController()
) {
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Screens
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegistrationScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Main App Screens
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCourses = { navController.navigate(Screen.Courses.route) },
                onNavigateToAssignments = { navController.navigate(Screen.Assignments.route) },
                onNavigateToExams = { navController.navigate(Screen.Exams.route) },
                onNavigateToSchedule = { navController.navigate(Screen.Schedule.route) },
                onNavigateToNotes = { navController.navigate(Screen.Notes.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Courses.route) {
            CoursesScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Assignments.route) {
            AssignmentsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Exams.route) {
            ExamsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Schedule.route) {
            ScheduleScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Notes.route) {
            NotesScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}