package com.example.weathersteam

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathersteam.handlers.steamLoginHandler
import com.example.weathersteam.ui.theme.LoginScreen
import com.example.weathersteam.ui.theme.MainScreen
import com.example.weathersteam.ui.theme.SignUpScreen
import com.example.weathersteam.ui.theme.SteamLoginScreen
import kotlinx.coroutines.launch

object AppRoutes {
    const val LOGIN = "login"
    const val STEAM_LOGIN = "steam_login"
    const val SIGN_UP = "signup"
    const val MAIN = "main"
}

@Composable
fun AppNavigation(context: Context?) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN) {

        // Login Screen Destination
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginClick = { email, password ->
                    println("Login attempt with $email")

                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(AppRoutes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(AppRoutes.SIGN_UP)
                },
                onSteamLoginClick = {
                    navController.navigate((AppRoutes.STEAM_LOGIN))
                }
            )
        }

        composable(AppRoutes.STEAM_LOGIN) {
            SteamLoginScreen(
                onSteamLoginClick = { formContent ->
                    coroutineScope.launch {
                        steamLoginHandler(context, formContent)
                    }
                    println("Steam login attempt with $formContent")
                },
                onRegisterClick = {
                    navController.navigate(AppRoutes.SIGN_UP)
                }
            )
        }

        // Sign Up Screen Destination
        composable(AppRoutes.SIGN_UP) {
            SignUpScreen(
                onSignUpClick = { email, password, confirmPassword ->
                    // TODO: Add your registration logic here
                    println("Sign up attempt with $email")
                },
                onLoginClick = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
            )
        }

        composable(AppRoutes.MAIN) {
            MainScreen(
                onLogoutClick = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.MAIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    AppNavigation(null)
}

