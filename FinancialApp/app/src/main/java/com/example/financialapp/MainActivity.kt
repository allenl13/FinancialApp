package com.example.financialapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.financialapp.notifications.EnsureNotificationsReady
import com.example.financialapp.ui.goal.GoalDetailScreen
import com.example.financialapp.ui.goal.GoalsListScreen
import com.example.financialapp.ui.theme.FinancialAppTheme

// Feature pages
import com.example.financialapp.Conversion.ConvertPage
import com.example.financialapp.Conversion.ConvertViewModel
import com.example.financialapp.Investment.InvestPage
import com.example.financialapp.Investment.InvestVMFactory
import com.example.financialapp.Investment.InvestViewModel
import com.example.financialapp.dashboard.MainScreen
import com.example.financialapp.repo.MainViewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinancialAppTheme {
                val nav = rememberNavController()

                // Set up notif channel + request POST_NOTIFICATIONS on Android 13+
                EnsureNotificationsReady()

                NavHost(
                    navController = nav,
                    startDestination = "main"
                ) {
                    composable("main") {
                        MainScreen(
                            expenses = mainViewModel.loadData(),
                            onConvertClick = { nav.navigate("convert") },
                            onInvestClick = { nav.navigate("invest") },
                            onSubsClick   = { /* nav.navigate("subscriptions") */ },
                            onGoalsClick  = { nav.navigate("goals") }
                        )
                    }

                    composable("convert") {
                        // Activity-scoped VM so it survives recompositions & navigation
                        ConvertPage(
                            ViewModelProvider(this@MainActivity)[ConvertViewModel::class.java]
                        )
                    }

                    composable("invest") {
                        // Provide repo via factory (needs Context)
                        val ctx = LocalContext.current
                        val vm: InvestViewModel = viewModel(factory = InvestVMFactory(ctx))
                        InvestPage(viewModel = vm)
                    }

                    composable("goals") {
                        GoalsListScreen(nav)   // list screen
                    }

                    composable(
                        route = "goal/{goalId}",
                        arguments = listOf(navArgument("goalId") { type = NavType.LongType })
                    ) {
                        GoalDetailScreen(nav)  // detail screen; reads goalId via SavedStateHandle
                    }

                    // composable("subscriptions") { /* TODO */ }
                }
            }
        }
    }
}