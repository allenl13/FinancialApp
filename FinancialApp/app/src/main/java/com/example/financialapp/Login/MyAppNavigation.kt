package com.example.financialapp.Login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financialapp.Login.pages.ForgotPassword
import com.example.financialapp.Login.pages.LoginPage
import com.example.financialapp.Login.pages.SignupPage
import com.example.financialapp.dashboard.MainScreen
import com.example.financialapp.repo.MainViewModel
import com.example.financialapp.ui.settings.BackgroundFixedViewModel

/**
 * Auth navigation (login / signup / forgot) + simple main route.
 * NOTE:
 * - bgVm is injected from the caller (e.g., MainActivity) so MainScreen shares the same instance
 *   with SettingsScreen and background changes apply instantly.
 */
@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    bgVm: BackgroundFixedViewModel
) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "login") {

        composable("login") {
            LoginPage(
                modifier = modifier,
                navController = nav,
                authViewModel = authViewModel
            )
        }

        composable("signup") {
            SignupPage(
                modifier = modifier,
                navController = nav,
                authViewModel = authViewModel
            )
        }

        // Home (simple) – uses the same bgVm instance
        composable("main") {
            val mainViewModel: MainViewModel = viewModel()
            MainScreen(
                expenses = mainViewModel.loadData(),
                onConvertClick = { nav.navigate("convert") },
                onInvestClick  = { nav.navigate("invest") },
                onSubsClick    = { nav.navigate("subscriptions") },
                onGoalsClick   = { nav.navigate("goals") },
                onSettingsClick = { nav.navigate("settings") },
                onCategoryClick = { nav.navigate("categories") },
                onChatClick     = { nav.navigate("chatpage") },
                onLogoutClick   = { nav.navigate("login") },
                bgVm = bgVm
            )
        }

        composable("forgot") {
            ForgotPassword(
                vm = authViewModel,
                onBackToLogin = { nav.popBackStack("login", inclusive = false) }
            )
        }
    }
}
