package com.example.financialapp

// Feature pages (use the correct package names)

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.example.financialapp.Investment.InvestViewModel
import com.example.financialapp.Login.AuthViewModel
import com.example.financialapp.Login.MyAppNavigation
import com.example.financialapp.Login.pages.LoginPage
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

         //val convertViewModel = ViewModelProvider(this)[ConvertViewModel::class.java]
         //val investViewModel  = ViewModelProvider(this)[InvestViewModel::class.java]

        setContent {
            AppRoot(
                 //convertViewModel = convertViewModel,
                 //investViewModel = investViewModel)

            )}
    }
}

@Composable
fun AppRoot(
   // convertViewModel: ConvertViewModel,
   // investViewModel: InvestViewModel
) {
    // App-wide theme + settings VMs
    val themeVm: ThemeViewModel = viewModel()
    val txVm: TransactionsViewModel = viewModel()

    val mode by themeVm.mode.collectAsState()
    val primary by themeVm.primaryArgb.collectAsState()
    val exportResult by txVm.exportResult.collectAsState()

    AppThemeExt(mode = mode, primaryArgb = primary) {
        EnsureNotificationsReady()
        EnsureSubNotificationsReady()

        val nav = rememberNavController()
        val ctx = LocalContext.current

        // Toast on CSV export result
        LaunchedEffect(exportResult) {
            exportResult?.let { res ->
                if (res.isSuccess) {
                    Toast.makeText(ctx, "CSV saved: ${res.getOrNull()}", Toast.LENGTH_LONG).show()
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

        Scaffold(modifier = Modifier.fillMaxSize()) { inner ->
            NavHost(
                navController = nav,
                startDestination = "login",
                modifier = Modifier.padding(inner)
            ) {
                // Subscriptions (start)
                composable("sub") { MainSub() }
                composable("chatpage") {
                    val chatvm: ChatViewModel = viewModel()
                    ChatPage(
                        viewModel = chatvm,   // whatever your VM variable is here
                        modifier = Modifier
                    )
                }
                composable("login")
                {
                    val authvm: AuthViewModel = viewModel() // or hiltViewModel() if using Hilt
                    MyAppNavigation(
                        modifier = Modifier,
                        authViewModel = authvm
                    )

                }

                // Categories / Goals
                composable("categories") { CategoryListScreen() }
                composable("goals") { GoalsListScreen(nav) }
                composable(
                    route = "goal/{goalId}",
                    arguments = listOf(navArgument("goalId") { type = NavType.LongType })
                ) {
                    GoalDetailScreen(nav)
                }

                // Feature routes
                 //composable("invest")  { InvestPage(investViewModel) }
                 //composable("convert") { ConvertPage(convertViewModel) }

                // Settings (theme + CSV export)
                composable("settings") {
                    SettingsScreen(
                        currentMode = mode,
                        currentPrimary = primary,
                        onChangeMode = themeVm::setMode,
                        onChangeColor = themeVm::setPrimaryColor,
                        onExportCsv = { txVm.exportCsv(ctx) }
                    )
                }
            }
        }
    }
}
