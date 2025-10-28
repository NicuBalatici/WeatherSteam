package com.example.weathersteam

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathersteam.ui.theme.LoginScreen
import com.example.weathersteam.ui.theme.SignUpScreen
import com.example.weathersteam.ui.theme.SteamLoginScreen

object AppRoutes {
    const val LOGIN = "login"
    const val STEAM_LOGIN = "steam_login"
    const val SIGN_UP = "signup"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN) {

        // Login Screen Destination
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginClick = { email, password ->
                    // TODO: Add your login authentication logic here
                    println("Login attempt with $email")
                },
                onRegisterClick = {
                    // This handles the navigation to the sign-up screen
                    navController.navigate(AppRoutes.SIGN_UP)
                },
                onSteamLoginClick = {
                    navController.navigate((AppRoutes.STEAM_LOGIN))
                }
            )
        }

        composable(AppRoutes.STEAM_LOGIN) {
            SteamLoginScreen(
                onSteamLoginClick = { steamId ->
                    // TODO: Add your login authentication logic here
                    println("Steam login attempt with $steamId")
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
                // This is the code that replaces the TODO
                onLoginClick = {
                    navController.navigate(AppRoutes.LOGIN) {
                        // Pop up to the login screen to avoid building a large back stack
                        popUpTo(AppRoutes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}

