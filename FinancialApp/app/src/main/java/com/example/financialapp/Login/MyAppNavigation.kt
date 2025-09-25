package com.example.financialapp.Login

import com.example.financialapp.Login.pages.ForgotPassword
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//use for homepage
//import com.example.financialapp.Login.pages.HomePage

import com.example.financialapp.Login.pages.LoginPage
import com.example.financialapp.Login.pages.SignupPage

//login/signup/forgor navigation
@Composable
fun MyAppNavigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            LoginPage(modifier,navController,authViewModel)
        }
        composable("signup"){
            SignupPage(modifier,navController,authViewModel)
        }
        //use for our home page
        //composable("home"){
            //HomePage(modifier,navController,authViewModel)
        //}
        composable("forgot")
        {
            ForgotPassword(
                vm = authViewModel,
                onBackToLogin = {navController.popBackStack("login", inclusive = false)}
            )
        }
    })
}