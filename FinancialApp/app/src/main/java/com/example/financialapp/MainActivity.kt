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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financialapp.ui.category.CategoryListScreen
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
                startDestination = "categories",
                modifier = Modifier.padding(inner)
            ) {
                composable("categories") { CategoryListScreen() }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppRootPreview() {
    AppRoot()
}
