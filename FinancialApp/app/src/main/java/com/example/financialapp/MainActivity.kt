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

// From Tristan's branch
import com.example.financialapp.Conversion.ConvertViewModel
import com.example.financialapp.Conversion.ConvertPage
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
                // Sets up notification channel and (on Android 13+) requests POST_NOTIFICATIONS once.
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
                            onSubsClick = { /* nav.navigate("subscriptions") */ },
                            onGoalsClick = { nav.navigate("goals") }
                        )
                    }

                    composable("convert") {
                        ConvertPage(
                            ViewModelProvider(this@MainActivity)[ConvertViewModel::class.java]
                        )
                    }

                    composable("invest") {
                        val ctx = LocalContext.current
                        val vm: InvestViewModel = viewModel(factory = InvestVMFactory(ctx))
                        InvestPage(viewModel = vm)
                    }

                    composable("goals") {
                        GoalsListScreen(nav)   // <- LIST screen
                    }

                    composable(
                        route = "goal/{goalId}",
                        arguments = listOf(navArgument("goalId") { type = NavType.LongType })
                    ) {
                        GoalDetailScreen(nav)  // <- DETAIL screen (now receives goalId via SavedStateHandle)
                    }

                    // navigate to subscription management
//                    composable("subscription") {
//
//                    }
                }
            }
        }
    }
}




/// keeping this incase needed can delete if not needed

// Keep Tristan's ViewModels (constructed in Activity and passed down)
//        val convertViewModel = ViewModelProvider(this)[ConvertViewModel::class.java]
//        val investViewModel = ViewModelProvider(this)[InvestViewModel::class.java]
//
//        setContent {
//            AppRoot(
//                convertViewModel = convertViewModel,
//                investViewModel = investViewModel
//            )
//        }
//    }
//}
//
//@Composable
//fun AppRoot(
//    convertViewModel: ConvertViewModel,
//    investViewModel: InvestViewModel
//) {
//    FinancialAppTheme {
//        // Sets up notification channel and (on Android 13+) requests POST_NOTIFICATIONS once.
//        EnsureNotificationsReady()
//
//        val nav = rememberNavController()
//        Scaffold(modifier = Modifier.fillMaxSize()) { inner ->
//            NavHost(
//                navController = nav,
//                startDestination = "goals",
//                modifier = Modifier.padding(inner)
//            ) {
//                // Existing destinations from main
//                composable("categories") { CategoryListScreen() }
//                composable("goals") { GoalsListScreen(nav) }
//                composable(
//                    route = "goal/{goalId}",
//                    arguments = listOf(navArgument("goalId") { type = NavType.LongType })
//                ) {
//                    GoalDetailScreen(nav)
//                }
//
//                // New destinations from Tristan's work
//                composable("invest") { InvestPage(investViewModel) }
//                composable("convert") { ConvertPage(convertViewModel) }
//            }
//        }
//    }
//}