package com.example.financialapp

// Feature pages (use the correct package names)
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.financialapp.Login.pages.AddMFAPage
import com.example.financialapp.Login.pages.ForgotPassword
import com.example.financialapp.Login.pages.LoginPage
import com.example.financialapp.Login.pages.SignupPage
import com.example.financialapp.dashboard.MainScreen
import com.example.financialapp.data.Transaction
import com.example.financialapp.notifications.EnsureNotificationsReady
import com.example.financialapp.notifications.EnsureSubNotificationsReady
import com.example.financialapp.repo.MainViewModel
import com.example.financialapp.report.ReportScreen
import com.example.financialapp.subscriptions.MainSub
import com.example.financialapp.ui.goal.GoalDetailScreen
import com.example.financialapp.ui.goal.GoalsListScreen
import com.example.financialapp.ui.settings.SettingsScreen
import com.example.financialapp.ui.theme.AppThemeExt
import com.example.financialapp.ui.theme.ThemeViewModel
import com.example.financialapp.ui.transactions.TransactionsViewModel
import com.example.financialapp.ui.settings.BackgroundFixedViewModel
import com.example.financialapp.wallet.AddCardScreen
import com.example.financialapp.wallet.CardDetailScreen
import com.example.financialapp.wallet.WalletHome
import com.example.financialapp.wallet.WalletListViewModel
import android.content.Context
import androidx.compose.runtime.*
import com.example.financialapp.Login.pages.MFAChallengePage

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val convertViewModel: ConvertViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainApp(mainViewModel, convertViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MainApp(
    mainViewModel: MainViewModel,
    convertViewModel: ConvertViewModel
) {
    // --- app-wide viewmodels ---
    val themeViewModel: ThemeViewModel = viewModel()
    val transactionsViewModel: TransactionsViewModel = viewModel()
    val backgroundFixedViewModel: BackgroundFixedViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val walletListViewModel: WalletListViewModel = viewModel()

    val mode by themeViewModel.mode.collectAsState()
    val primary by themeViewModel.primaryArgb.collectAsState()
    val exportResult by transactionsViewModel.exportResult.collectAsState()

    AppThemeExt(
        mode = mode,
        primaryArgb = primary
    ) {
        val navController = rememberNavController()
        val context = LocalContext.current

        SetupNotifications()
        HandleCsvExportToast(exportResult as Result<String>?, transactionsViewModel, context)

        // --- NavHost ---
        NavHost(navController = navController, startDestination = "login") {

            composable("main") {
                MainScreen(
                    onCardClick = { navController.navigate("wallet") },
                    onCardsClick = { id -> navController.navigate("card/$id") },
                    walletListViewModel = walletListViewModel,
                    expenses = mainViewModel.loadData(),
                    onConvertClick = { navController.navigate("convert") },
                    onInvestClick = { navController.navigate("invest") },
                    onSubsClick = { navController.navigate("subscriptions") },
                    onGoalsClick = { navController.navigate("goals") },
                    onSettingsClick = { navController.navigate("settings") },
                    onReportClick = { navController.navigate("report") },
                    onChatClick = { navController.navigate("chatpage") },
                    onLogoutClick = {
                        authViewModel.signout()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                            restoreState = false
                        }
                    },
                    bgVm = backgroundFixedViewModel
                ) { navController.navigate("login") }
            }

                    composable("convert") {
                        // Activity-scoped VM so it survives recompositions & navigation
                        ConvertPage(convertViewModel)
                    }

                    composable("invest") {
                        // Provide repo via factory
                        val ctx = LocalContext.current
                        val vm: InvestViewModel = viewModel(factory = InvestVMFactory(ctx))
                        InvestPage(viewModel = vm)
                    }

                    composable("goals") {
                        GoalsListScreen(navController)   // list screen
                    }

                    composable(
                        route = "goal/{goalId}",
                        arguments = listOf(navArgument("goalId") { type = NavType.LongType })
                    ) {
                        GoalDetailScreen(navController)  // detail screen; reads goalId via SavedStateHandle
                    }

                    composable("subscriptions") {
                        MainSub(exit = { navController.popBackStack() })
                    }

                    // Settings (theme + CSV export)
                    composable("settings") {
                        SettingsScreen(
                            currentMode = mode,
                            currentPrimary = primary,
                            onChangeMode = themeViewModel::setMode,
                            onChangeColor = themeViewModel::setPrimaryColor,
                            onExportCsv = {
                                val expenses = mainViewModel.loadData()
                                val txs = expenses.map { e ->
                                    Transaction(
                                        date = e.time,
                                        amount = e.price,
                                        category = e.title,
                                    )
                                }
                                transactionsViewModel.exportCsv(context)
                            },
                            bgVm = backgroundFixedViewModel,
                            onAddMfa = {
                                navController.navigate("addMfa")
                            }
                        )
                    }


                    //Ai page
                    composable ("chatpage") {
                        val chatvm: ChatViewModel = viewModel()
                        ChatPage(
                            viewModel = chatvm,
                            modifier = Modifier
                        )
                    }

                    // ------ Auth flow ------
                    composable("login") {
                        LoginPage(
                            navController = navController,
                            authViewModel = authViewModel   // <- use shared
                        )
                    }
                    composable("signup") {
                        SignupPage(
                            navController = navController,
                            authViewModel = authViewModel   // <- use shared
                        )
                    }
                    composable("forgot") {
                        ForgotPassword(
                            vm = authViewModel,             // <- use shared
                            onBackToLogin = { navController.popBackStack("login", inclusive = false) }
                        )
                    }
                    composable("mfa") {
                        val act = (LocalContext.current as? Activity) ?: return@composable
                        MFAChallengePage(
                            vm = authViewModel,   // shared VM (has mfaResolver)
                            activity = act,
                            onSuccess = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable("addMfa") {
                        val act = (LocalContext.current as? Activity) ?: return@composable
                        AddMFAPage(
                            vm = authViewModel,        // use the shared VM
                            activity = act,
                            onDone = { navController.popBackStack() } // back to settings after ENROLL_SUCCESS
                        )
                    }





            composable("report"){
                        ReportScreen()
                    }

                    // Wallet home
                    composable("wallet") {
                        WalletHome(
                            navController,
                            expenses = viewModel<MainViewModel>().loadData(),
                        )
                    }
                    // Add/Edit Cards
                    composable("addCard") {
                        AddCardScreen(
                            onDone = { navController.popBackStack() },
                            vm = walletListViewModel
                        )
                    }

                    // Card detail
                    composable(
                        route = "card/{cardId}",
                        arguments = listOf(navArgument("cardId") { type = NavType.LongType })
                    ) {
                        val id = it.arguments?.getLong("cardId") ?: return@composable
                        CardDetailScreen(nav = navController, cardId = id, vm = walletListViewModel)
                    }
                }
            }
        }

@Composable
private fun HandleCsvExportToast(
    exportResult: Result<String>?,
    transactionsViewModel: TransactionsViewModel,
    context: Context
) {
    LaunchedEffect(exportResult) {
        exportResult?.let { result ->
            if (result.isSuccess) {
                Toast.makeText(
                    context,
                    "CSV saved: ${result.getOrNull()}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Export failed: ${result.exceptionOrNull()?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            transactionsViewModel.consumeExportResult()
        }
    }
}

@Composable
fun SetupNotifications() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        EnsureNotificationsReady()
        EnsureSubNotificationsReady()
    }
}
