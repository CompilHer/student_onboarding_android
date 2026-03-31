package com.example.studentonboarding.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Loading : Screen("loading_screen")
    object Stage1Docs : Screen("stage_1_docs")
    object Stage2Payment : Screen("stage_2_payment")
    object Stage3Courses : Screen("stage_3_courses")
    object Stage4Accommodation : Screen("stage_4_accommodation")
    object Stage5ItSetup : Screen("stage_5_it_setup")
    object Stage6Compliance : Screen("stage_6_compliance")
    object Stage7IdCard : Screen("stage_7_id_card")
    object Stage8Review : Screen("stage_8_review")
    object StudentDashboard : Screen("student_dashboard")
    object Chatbot : Screen("chatbot_screen")
}