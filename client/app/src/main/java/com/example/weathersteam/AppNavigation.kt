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

object AppRoutes {
    const val LOGIN = "login"
    const val MAIN = "main"
    const val REGISTER = "register"
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

        composable(route = AppRoutes.MAIN) {
            MainScreen(
                onLogoutClick = {
                    sessionManager?.logout()
                    navController.navigate(route = AppRoutes.LOGIN) {
                        popUpTo(route = AppRoutes.LOGIN) {
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