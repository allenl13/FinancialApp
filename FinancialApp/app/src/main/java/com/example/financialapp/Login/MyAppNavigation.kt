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

// Screens
import com.example.financialapp.Login.pages.AddMFAPage
import com.example.financialapp.Login.pages.ForgotPassword
import com.example.financialapp.Login.pages.LoginPage
import com.example.financialapp.Login.pages.MFAChallengePage
import com.example.financialapp.Login.pages.SignupPage
import com.example.financialapp.dashboard.MainScreen

// VMs + deps
import com.example.financialapp.repo.MainViewModel
import com.example.financialapp.ui.settings.BackgroundFixedViewModel
import com.example.financialapp.wallet.WalletListViewModel

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    bgVm: BackgroundFixedViewModel
) {
    val nav = rememberNavController()
    val walletVm: WalletListViewModel = viewModel()

    NavHost(navController = nav, startDestination = "login") {

        // -------- Login --------
        composable("login") {
            LoginPage(
                modifier = modifier,
                navController = nav,
                authViewModel = authViewModel
            )

            // react to auth + MFA states while on login screen
            val authState by authViewModel.authState.observeAsState(AuthState.Unauthenticated)
            val mfaState by authViewModel.mfaState.observeAsState("")

            // If primary sign-in succeeded, decide whether to enroll MFA or go home
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

            // If MFA is required during sign-in, go to the challenge screen
            LaunchedEffect(mfaState) {
                when (mfaState) {
                    "MFA_REQUIRED" -> nav.navigate("mfaChallenge")
                    "MFA_SUCCESS" -> nav.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }

        // -------- Signup --------
        composable("signup") {
            SignupPage(
                modifier = modifier,
                navController = nav,
                authViewModel = authViewModel
            )
        }

        // -------- Forgot Password --------
        composable("forgot") {
            ForgotPassword(
                vm = authViewModel,
                onBackToLogin = { nav.popBackStack("login", inclusive = false) }
            )
        }

        // -------- MFA Enrollment (one-time) --------
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

        // -------- MFA Challenge (sign-in step-up) --------
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

        // -------- Home --------
        composable("main") {
            val mainViewModel: MainViewModel = viewModel()
            MainScreen(
                onCardClick = { nav.navigate("wallet") },
                onCardsClick = { id -> nav.navigate("card/$id") },
                walletListViewModel = walletVm,
                expenses = mainViewModel.loadData(),
                onConvertClick = { nav.navigate("convert") },
                onInvestClick = { nav.navigate("invest") },
                onSubsClick = { nav.navigate("subscriptions") },
                onGoalsClick = { nav.navigate("goals") },
                onSettingsClick = { nav.navigate("settings") },
                onChatClick = { nav.navigate("chatpage") },
                onReportClick = { nav.navigate("report") },
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
