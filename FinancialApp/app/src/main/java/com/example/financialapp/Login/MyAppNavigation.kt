package com.example.financialapp.Login

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financialapp.Login.AuthState
import com.example.financialapp.Login.AuthViewModel
import com.example.financialapp.Login.pages.AddMFAPage
import com.example.financialapp.Login.pages.ForgotPassword
import com.example.financialapp.Login.pages.LoginPage
import com.example.financialapp.Login.pages.MFAChallengePage
import com.example.financialapp.Login.pages.SignupPage
import com.example.financialapp.dashboard.MainScreen
import com.example.financialapp.repo.MainViewModel
import com.example.financialapp.ui.settings.BackgroundFixedViewModel
import com.example.financialapp.wallet.WalletListViewModel

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
    val walletVm: WalletListViewModel = viewModel()

    NavHost(navController = nav, startDestination = "login") {
        composable("login") {
            LoginPage(
                modifier = modifier,
                navController = nav,
                authViewModel = authViewModel
            )

            // React to auth + MFA states while on login screen
            val authState by authViewModel.authState.observeAsState(AuthState.Unauthenticated)
            val mfaState by authViewModel.mfaState.observeAsState("")

            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    if (authViewModel.needsMfaEnrollment()) {
                        nav.navigate("addMfa")
                    } else {
                        nav.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            }

            LaunchedEffect(mfaState) {
                when (mfaState) {
                    "MFA_REQUIRED" -> nav.navigate("mfaChallenge")
                    "MFA_SUCCESS" -> nav.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }

        composable("signup") {
            SignupPage(
                modifier = modifier,
                navController = nav,
                authViewModel = authViewModel
            )
        }

        composable("forgot") {
            ForgotPassword(
                vm = authViewModel,
                onBackToLogin = { nav.popBackStack("login", inclusive = false) }
            )
        }

        // MFA enrollment page (one-time)
        composable("addMfa") {
            val activity = LocalContext.current as Activity
            AddMFAPage(
                vm = authViewModel,
                activity = activity
            ) {
                nav.navigate("main") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        // MFA challenge page
        composable("mfaChallenge") {
            val activity = LocalContext.current as Activity
            MFAChallengePage(
                vm = authViewModel,
                activity = activity
            ) {
                nav.navigate("main") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        // Home
        composable("main") {
            val mainViewModel: MainViewModel = viewModel()
            MainScreen(
                onCardClick = { nav.navigate("wallet") },
                onCardsClick = { id -> nav.navigate("card/$id") },
                walletVm = walletVm,
                expenses = mainViewModel.loadData(),
                onConvertClick = { nav.navigate("convert") },
                onInvestClick = { nav.navigate("invest") },
                onSubsClick = { nav.navigate("subscriptions") },
                onGoalsClick = { nav.navigate("goals") },
                onSettingsClick = { nav.navigate("settings") },
                onChatClick = { nav.navigate("chatpage") },
                onCategoryClick = { nav.navigate("categories") },
                onLogoutClick = {
                    nav.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                enableBackground = true,
                bgVm = bgVm,
                function = {}
            )
        }
    }
}