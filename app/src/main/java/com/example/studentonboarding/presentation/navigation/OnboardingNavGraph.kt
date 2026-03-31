package com.example.studentonboarding.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studentonboarding.presentation.screens.login.LoginScreen
import com.example.studentonboarding.presentation.screens.stage1_docs.Stage1DocsScreen
import com.example.studentonboarding.presentation.screens.stage2_payment.Stage2PaymentScreen

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

        // 2. Stage 1: Document Upload
        composable(route = Screen.Stage1Docs.route) {
            Stage1DocsScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Stage2Payment.route) {
                        // Pop Stage 1 so they don't accidentally navigate backwards into it
                        popUpTo(Screen.Stage1Docs.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. Stage 2: Fee Payment
        composable(route = Screen.Stage2Payment.route) {
            Stage2PaymentScreen(
                onNavigateNext = {
                    // We will route to Stage 3 later!
                }
            )
        }
    }
}
