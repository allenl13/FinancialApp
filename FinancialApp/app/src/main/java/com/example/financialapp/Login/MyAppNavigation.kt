package com.example.financialapp.Login

import com.example.financialapp.Login.pages.ForgotPassword
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//use for homepage
//import com.example.financialapp.Login.pages.HomePage

import com.example.financialapp.Login.pages.LoginPage
import com.example.financialapp.Login.pages.SignupPage
import com.example.financialapp.dashboard.MainScreen
import com.example.financialapp.repo.MainViewModel
import com.example.financialapp.wallet.WalletListViewModel

//login/signup/forgor navigation
@Composable
fun MyAppNavigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel) {
    val nav = rememberNavController()
    val walletVm: WalletListViewModel = viewModel()

    NavHost(navController = nav, startDestination = "login", builder = {
        composable("login"){
            LoginPage(modifier,nav,authViewModel)
        }
        composable("signup"){
            SignupPage(modifier,nav,authViewModel)
        }
        //use for our home page
        composable("main") {
            val mainViewModel: MainViewModel = viewModel()
            MainScreen(
                onCardClick = { nav.navigate("wallet") },
                expenses = mainViewModel.loadData(),
                onConvertClick = { nav.navigate("convert") },
                onInvestClick = { nav.navigate("invest") },
                onSubsClick = { nav.navigate("subscriptions") },
                onGoalsClick = { nav.navigate("goals") },
                onSettingsClick = { nav.navigate("settings") },
                onChatClick = { nav.navigate("chatpage") },
                onCategoryClick = { nav.navigate("categories") },
                onLogoutClick   = { nav.navigate("login") { popUpTo("login") { inclusive = true } } },
                onAddCardClick = { nav.navigate("addCard") },
                walletVm        = walletVm
            ) { nav.navigate("login") }
        }
        composable("forgot")
        {
            ForgotPassword(
                vm = authViewModel,
                onBackToLogin = {nav.popBackStack("login", inclusive = false)}
            )
        }
    })
}