package com.example.studentonboarding.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studentonboarding.presentation.screens.login.LoginScreen
import com.example.studentonboarding.presentation.screens.loading.LoadingScreen // ADD THIS
import com.example.studentonboarding.presentation.screens.stage1_docs.Stage1DocsScreen
import com.example.studentonboarding.presentation.screens.stage2_payment.Stage2PaymentScreen
import com.example.studentonboarding.presentation.screens.stage3_courses.Stage3CoursesScreen
import com.example.studentonboarding.presentation.screens.stage4_accommodation.Stage4AccommodationScreen

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
                    // CHANGE: Now we go to Loading instead of Stage 1
                    navController.navigate(Screen.Loading.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. The Dynamic Router / Loading Screen
        composable(route = Screen.Loading.route) {
            LoadingScreen(
                onNavigateToStage1 = {
                    navController.navigate(Screen.Stage1Docs.route) { popUpTo(Screen.Loading.route) { inclusive = true } }
                },
                onNavigateToStage2 = {
                    navController.navigate(Screen.Stage2Payment.route) { popUpTo(Screen.Loading.route) { inclusive = true } }
                },
                onNavigateToStage3 = {
                    navController.navigate(Screen.Stage3Courses.route) { popUpTo(Screen.Loading.route) { inclusive = true } }
                },
                onNavigateToStage4 = {
                    navController.navigate(Screen.Stage4Accommodation.route) { popUpTo(Screen.Loading.route) { inclusive = true } }
                }
            )
        }

        // 3. Stage 1: Document Upload
        composable(route = Screen.Stage1Docs.route) {
            Stage1DocsScreen(
                onNavigateNext = {
                    // Force the app to re-evaluate the state after a stage is done
                    navController.navigate(Screen.Loading.route) {
                        popUpTo(Screen.Stage1Docs.route) { inclusive = true }
                    }
                }
            )
        }

        // 4. Stage 2: Fee Payment
        composable(route = Screen.Stage2Payment.route) {
            Stage2PaymentScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Loading.route) {
                        popUpTo(Screen.Stage2Payment.route) { inclusive = true }
                    }
                }
            )
        }

        // 5. Stage 3: Course Registration
        composable(route = Screen.Stage3Courses.route) {
            Stage3CoursesScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Loading.route) {
                        popUpTo(Screen.Stage3Courses.route) { inclusive = true }
                    }
                }
            )
        }

        // 6. Stage 4: Accommodation
        composable(route = Screen.Stage4Accommodation.route) {
            Stage4AccommodationScreen(
                onNavigateNext = {
                    // navController.navigate(Screen.Loading.route) ... for when Stage 5 is ready
                }
            )
        }
    }
}