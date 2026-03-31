package com.example.studentonboarding.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studentonboarding.presentation.screens.login.LoginScreen
import com.example.studentonboarding.presentation.screens.loading.LoadingScreen
import com.example.studentonboarding.presentation.screens.stage1_docs.Stage1DocsScreen
import com.example.studentonboarding.presentation.screens.stage2_payment.Stage2PaymentScreen
import com.example.studentonboarding.presentation.screens.stage3_courses.Stage3CoursesScreen
import com.example.studentonboarding.presentation.screens.stage4_accommodation.Stage4AccommodationScreen
import com.example.studentonboarding.presentation.screens.stage5_it_setup.Stage5ItSetupScreen
import com.example.studentonboarding.presentation.screens.stage6_compliance.Stage6ComplianceScreen
import com.example.studentonboarding.presentation.screens.stage7_id_card.Stage7IdCardScreen
import com.example.studentonboarding.presentation.screens.stage8_review.Stage8ReviewScreen
import com.example.studentonboarding.presentation.screens.dashboard.StudentDashboardScreen
import com.example.studentonboarding.presentation.screens.chatbot.ChatbotScreen

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
                    navController.navigate(Screen.Loading.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. The API Fetcher / Router
        composable(route = Screen.Loading.route) {
            LoadingScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.StudentDashboard.route) {
                        popUpTo(Screen.Loading.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. The Main Student Hub
        composable(route = Screen.StudentDashboard.route) {
            StudentDashboardScreen(
                onNavigateToStage = { stageNumber ->
                    val route = when(stageNumber) {
                        1 -> Screen.Stage1Docs.route
                        2 -> Screen.Stage2Payment.route
                        3 -> Screen.Stage3Courses.route
                        4 -> Screen.Stage4Accommodation.route
                        5 -> Screen.Stage5ItSetup.route
                        6 -> Screen.Stage6Compliance.route
                        7 -> Screen.Stage7IdCard.route
                        8 -> Screen.Stage8Review.route
                        else -> Screen.Stage1Docs.route
                    }
                    navController.navigate(route)
                },
                onNavigateToLogin = {
                    // Logs user out and clears the entire navigation backstack
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToChatbot = {
                    navController.navigate(Screen.Chatbot.route)
                }
            )
        }

        // --- FSM STAGES (All route back to Loading to refresh state upon completion) ---

        composable(route = Screen.Stage1Docs.route) {
            Stage1DocsScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Loading.route) { popUpTo(Screen.Stage1Docs.route) { inclusive = true } }
                }
            )
        }

        composable(route = Screen.Stage2Payment.route) {
            Stage2PaymentScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Loading.route) { popUpTo(Screen.Stage2Payment.route) { inclusive = true } }
                }
            )
        }

        composable(route = Screen.Stage3Courses.route) {
            Stage3CoursesScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Loading.route) { popUpTo(Screen.Stage3Courses.route) { inclusive = true } }
                }
            )
        }

        composable(route = Screen.Stage4Accommodation.route) {
            Stage4AccommodationScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Loading.route) { popUpTo(Screen.Stage4Accommodation.route) { inclusive = true } }
                }
            )
        }

        composable(route = Screen.Stage5ItSetup.route) {
            Stage5ItSetupScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Loading.route) { popUpTo(Screen.Stage5ItSetup.route) { inclusive = true } }
                }
            )
        }

        composable(route = Screen.Stage6Compliance.route) {
            Stage6ComplianceScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Loading.route) { popUpTo(Screen.Stage6Compliance.route) { inclusive = true } }
                }
            )
        }

        composable(route = Screen.Stage7IdCard.route) {
            Stage7IdCardScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Loading.route) { popUpTo(Screen.Stage7IdCard.route) { inclusive = true } }
                }
            )
        }

        composable(route = Screen.Stage8Review.route) {
            Stage8ReviewScreen(
                onNavigateToSuccess = {
                    navController.navigate(Screen.Loading.route) { popUpTo(Screen.Stage8Review.route) { inclusive = true } }
                }
            )
        }

        composable(route = Screen.Chatbot.route) {
            ChatbotScreen(
                onNavigateBack = {
                    navController.popBackStack() // Goes back to the Dashboard
                }
            )
        }
    }
}