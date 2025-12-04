package com.example.weathersteam

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathersteam.helpers.SessionManager
import com.example.weathersteam.ui.theme.LoginScreen
import com.example.weathersteam.ui.theme.MainScreen
import com.example.weathersteam.ui.theme.SignUpScreen
import com.example.weathersteam.ui.theme.WeatherScreen
import com.example.weathersteam.ui.theme.RandomGameScreen
import com.example.weathersteam.ui.theme.MyGamesScreen

object AppRoutes {
    const val LOGIN = "login"
    const val MAIN = "main"
    const val REGISTER = "register"
    const val WEATHER = "weather_choice"
    const val RANDOM = "random_choice"
    const val MY_GAMES = "my_games"
}

@Composable
fun AppNavigation(context: Context?) {
    val navController = rememberNavController()
    val sessionManager = context?.let { SessionManager(it) }

    val startRoute = if (sessionManager?.isTokenExpired() == true) AppRoutes.LOGIN else AppRoutes.MAIN

    NavHost(navController = navController, startDestination = startRoute) {

        composable(route = AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(route = AppRoutes.MAIN) {
                        popUpTo(route = AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(AppRoutes.REGISTER) }
            )
        }

        composable(route = AppRoutes.REGISTER) {
            SignUpScreen(
                onRegisterSuccess = { navController.navigate(AppRoutes.LOGIN) },
                onLoginClick = { navController.popBackStack() }
            )
        }

        // --- MAIN MENU ---
        composable(route = AppRoutes.MAIN) {
            val currentUsername = sessionManager?.fetchUserFromToken() ?: "Guest"
            MainScreen(
                username = currentUsername,
                onWeatherChoiceClick = { navController.navigate(AppRoutes.WEATHER) },
                onRandomChoiceClick = { navController.navigate(AppRoutes.RANDOM) },
                onMyGamesClick = { navController.navigate(AppRoutes.MY_GAMES) },
                onLogoutClick = {
                    sessionManager?.logout()
                    navController.navigate(route = AppRoutes.LOGIN) {
                        popUpTo(route = AppRoutes.MAIN) { inclusive = true }
                    }
                }
            )
        }

        // --- WEATHER SCREEN ---
        composable(route = AppRoutes.WEATHER) {
            WeatherScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- RANDOM SCREEN ---
        composable(route = AppRoutes.RANDOM) {
            RandomGameScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- MY GAMES SCREEN ---
        composable(route = AppRoutes.MY_GAMES) {
            MyGamesScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}