package com.example.weathersteam.viewmodels

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathersteam.ui.theme.LoginScreen
import com.example.weathersteam.ui.theme.MainScreen
 import com.example.weathersteam.ui.theme.SignUpScreen

object AppRoutes {
    const val LOGIN = "login"
    const val MAIN = "main"
    const val REGISTER = "register"
}

@Composable
fun AppNavigation(context: Context?) {
    val navController = rememberNavController()
    // val coroutineScope = rememberCoroutineScope() // Not strictly needed for navigation

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN) {

        composable(route = AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(route = AppRoutes.MAIN) {
                        popUpTo(route = AppRoutes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(AppRoutes.REGISTER)
                }
            )
        }

        // 2. THE MAIN DASHBOARD SCREEN
        composable(route = AppRoutes.MAIN) {
            MainScreen(
                onLogoutClick = {
                    navController.navigate(route = AppRoutes.LOGIN) {
                        popUpTo(route = AppRoutes.MAIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(route = AppRoutes.REGISTER) {
            SignUpScreen(
                onRegisterSuccess = {
                    navController.navigate(AppRoutes.LOGIN)
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

    }
}