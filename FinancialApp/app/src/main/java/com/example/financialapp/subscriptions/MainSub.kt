package com.example.financialapp.subscriptions

/*
*
*/

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financialapp.subscriptions.Routes.ADD
import com.example.financialapp.subscriptions.Routes.HOME
import com.example.financialapp.subscriptions.Routes.SUBS

object Routes {
    const val HOME = "home"
    const val SUBS = "subs"
    const val ADD = "add"

}

@Composable
fun MainSub() {
    val navController = rememberNavController()

    Surface(color = MaterialTheme.colorScheme.background) {
        NavHost(navController = navController, startDestination = SUBS) {
            composable(HOME) {
                HomeScreen(onOpenSubs = {
                    navController.navigate(SUBS)
                })
            }
            composable(SUBS) {
                SubscriptionScreen(navController)
            }
            composable(ADD) {
                AddSubscriptionScreen(navController)
            }
        }
    }

}