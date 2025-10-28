package com.example.financialapp.Login.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financialapp.Login.AuthViewModel
import com.example.financialapp.Login.AuthState
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val mfaState  = authViewModel.mfaState.observeAsState("")   // ▶ observe MFA state
    val context = LocalContext.current

    // Navigate to main on normal auth success / show errors
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("main") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
            else -> Unit
        }
    }

    // ▶ If password step succeeds but MFA is required, go to MFA screen
    LaunchedEffect(mfaState.value) {
        if (mfaState.value == "MFA_REQUIRED") {
            navController.navigate("mfa") { launchSingleTop = true }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", fontSize = 32.sp)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        val isLoading = authState.value is AuthState.Loading
        Button(
            onClick = { authViewModel.login(email, password) },
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            Text(if (isLoading) "Logging in..." else "Login")
        }

        // ▶ (optional) tiny debug helper while testing
        // if (mfaState.value.isNotBlank()) Text("mfaState=${mfaState.value}", fontSize = 12.sp)

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("forgot") }) { Text("Forgot password?") }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("signup") }) { Text("Create an account") }
    }
}
