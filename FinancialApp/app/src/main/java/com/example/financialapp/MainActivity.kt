package com.example.financialapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.ViewModelProvider
import com.example.financialapp.notifications.EnsureNotificationsReady
import com.example.financialapp.ui.category.CategoryListScreen
import com.example.financialapp.ui.goal.GoalDetailScreen
import com.example.financialapp.ui.goal.GoalsListScreen
import com.example.financialapp.ui.theme.FinancialAppTheme

// Feature pages
import com.example.financialapp.Conversion.ConvertViewModel
import com.example.financialapp.Convertion.ConvertPage
import com.example.financialapp.Investment.InvestPage
import com.example.financialapp.Investment.InvestViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val convertViewModel = ViewModelProvider(this)[ConvertViewModel::class.java]
        val investViewModel = ViewModelProvider(this)[InvestViewModel::class.java]

        setContent {
            AppRoot(
                convertViewModel = convertViewModel,
                investViewModel = investViewModel
            )
        }
    }
}

@Composable
fun AppRoot(
    convertViewModel: ConvertViewModel,
    investViewModel: InvestViewModel
) {
    FinancialAppTheme {
        // Notification channel + runtime request on Android 13+
        EnsureNotificationsReady()

        val nav = rememberNavController()
        Scaffold(modifier = Modifier.fillMaxSize()) { inner ->
            NavHost(
                navController = nav,
                startDestination = "goals",
                modifier = Modifier.padding(inner)
            ) {
                // Existing
                composable("categories") { CategoryListScreen() }
                composable("goals") { GoalsListScreen(nav) }
                composable(
                    route = "goal/{goalId}",
                    arguments = listOf(navArgument("goalId") { type = NavType.LongType })
                ) { GoalDetailScreen(nav) }

                // Feature routes
                composable("invest") { InvestPage(investViewModel) }
                composable("convert") { ConvertPage(convertViewModel) }
            }
        }
    }
}
