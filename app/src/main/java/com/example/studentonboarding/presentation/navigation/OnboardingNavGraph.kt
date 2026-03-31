package com.example.studentonboarding.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studentonboarding.presentation.screens.login.LoginScreen

@Composable
fun OnboardingNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToDashboard = {
                    // When login succeeds, we pop the login screen off the backstack
                    // so the user can't press the physical "Back" button to go back to it.
                    navController.navigate(Screen.Stage1Docs.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Stage 1: Document Upload (We will build this UI next)
        composable(route = Screen.Stage1Docs.route) {
            // Temporary placeholder until we build the real screen
            androidx.compose.material3.Text(
                text = "Welcome to Stage 1: Document Upload",
                color = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}