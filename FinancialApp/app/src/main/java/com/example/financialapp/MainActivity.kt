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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.financialapp.ui.category.CategoryListScreen
import com.example.financialapp.ui.goal.GoalDetailScreen
import com.example.financialapp.ui.goal.GoalsListScreen
import com.example.financialapp.ui.theme.FinancialAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppRoot() }
    }
}

@Composable
fun AppRoot() {
    FinancialAppTheme {
        val nav = rememberNavController()
        Scaffold(modifier = Modifier.fillMaxSize()) { inner ->
            NavHost(
                navController = nav,
                startDestination = "goals",
                modifier = Modifier.padding(inner)
            ) {
                composable("categories") { CategoryListScreen() }

                composable("goals") { GoalsListScreen(nav) }

                composable(
                    route = "goal/{goalId}",
                    arguments = listOf(navArgument("goalId") { type = NavType.LongType })
                ) { GoalDetailScreen(nav) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppRootPreview() { AppRoot() }
