package com.example.financialapp

// Feature pages (use the correct package names)



import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.financialapp.Ai.ChatPage
import com.example.financialapp.Ai.ChatViewModel
import com.example.financialapp.Conversion.ConvertPage
import com.example.financialapp.Conversion.ConvertViewModel
import com.example.financialapp.Investment.InvestPage
import com.example.financialapp.Investment.InvestVMFactory
import com.example.financialapp.Investment.InvestViewModel
import com.example.financialapp.Login.AuthViewModel
import com.example.financialapp.Login.pages.ForgotPassword
import com.example.financialapp.Login.pages.LoginPage
import com.example.financialapp.Login.pages.SignupPage
import com.example.financialapp.dashboard.MainScreen
import com.example.financialapp.data.Transaction
import com.example.financialapp.notifications.EnsureNotificationsReady
import com.example.financialapp.notifications.EnsureSubNotificationsReady
import com.example.financialapp.subscriptions.MainSub
import com.example.financialapp.ui.category.CategoryListScreen
import com.example.financialapp.ui.goal.GoalDetailScreen
import com.example.financialapp.ui.goal.GoalsListScreen
import com.example.financialapp.ui.settings.SettingsScreen
import com.example.financialapp.ui.theme.AppThemeExt
import com.example.financialapp.ui.theme.ThemeViewModel
import com.example.financialapp.ui.transactions.TransactionsViewModel
import com.example.financialapp.repo.MainViewModel
import com.example.financialapp.ui.settings.BackgroundFixedViewModel



class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val convertViewModel: ConvertViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // App-wide theme + settings VMs
            val themeVm: ThemeViewModel = viewModel()
            val txVm: TransactionsViewModel = viewModel()
            val bgVm: BackgroundFixedViewModel = viewModel()


            val mode by themeVm.mode.collectAsState()
            val primary by themeVm.primaryArgb.collectAsState()
            val exportResult by txVm.exportResult.collectAsState()
            val authvm: AuthViewModel = viewModel() //signout

            AppThemeExt(
                mode = mode,
                primaryArgb = primary
            ){
                val nav = rememberNavController()
                val ctx = LocalContext.current

                // Set up notif channel + request POST_NOTIFICATIONS on Android 13+
                EnsureNotificationsReady()
                EnsureSubNotificationsReady()

                // Toast on CSV export result
                LaunchedEffect(exportResult) {
                    exportResult?.let { res ->
                        if (res.isSuccess) {
                            Toast.makeText(ctx, "CSV saved: ${res.getOrNull()}", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            Toast.makeText(
                                ctx,
                                "Export failed: ${res.exceptionOrNull()?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        txVm.consumeExportResult()
                    }
                }
                NavHost(
                    navController = nav,
                    startDestination = "login"
                ) {

                    composable("main") {
                        MainScreen(
                            expenses = mainViewModel.loadData(),
                            onConvertClick = { nav.navigate("convert") },
                            onInvestClick = { nav.navigate("invest") },
                            onSubsClick   = { nav.navigate("subscriptions") },
                            onGoalsClick  = { nav.navigate("goals") },
                            onSettingsClick = { nav.navigate("settings") },
                            onCategoryClick = { nav.navigate("categories") },
                            onChatClick = { nav.navigate("chatpage") },
                            onLogoutClick = {
                                authvm.signout()
                                nav.navigate("login") {
                                    popUpTo(nav.graph.startDestinationId) { inclusive = true }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            },
                            bgVm = bgVm
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

                    composable("subscriptions") {
                        MainSub()
                    }

                    // Settings (theme + CSV export)
                    composable("settings") {
                        SettingsScreen(
                            currentMode = mode,
                            currentPrimary = primary,
                            onChangeMode = themeVm::setMode,
                            onChangeColor = themeVm::setPrimaryColor,
                            onExportCsv = {
                                val expenses = mainViewModel.loadData()
                                val txs = expenses.map { e ->
                                    Transaction(
                                        date = e.time,
                                        amount = e.price,
                                        category = e.title,
                                    )
                                }
                                txVm.exportCsv(ctx)
                            },
                            bgVm = bgVm
                        )
                    }

                    //Ai page
                    composable ("chatpage") {
                        val chatvm: ChatViewModel = viewModel()
                        ChatPage(
                            viewModel = chatvm,   // whatever your VM variable is here
                            modifier = Modifier
                        )
                    }

                    //start on login page
                    // ------ Auth flow ------
                    composable("login") {
                        val authvm: AuthViewModel = viewModel()
                        LoginPage(
                            navController = nav,
                            authViewModel = authvm
                        )
                    }
                    composable("signup") {
                        val authvm: AuthViewModel = viewModel()
                        SignupPage(
                            navController = nav,
                            authViewModel = authvm
                        )
                    }
                    composable("forgot") {
                        val authvm: AuthViewModel = viewModel()
                        ForgotPassword(
                            vm = authvm,
                            onBackToLogin = { nav.popBackStack("login", inclusive = false) }
                        )
                    }

                    //categories page
                    composable("categories"){
                        CategoryListScreen()
                    }
                }
            }
        }
    }
}