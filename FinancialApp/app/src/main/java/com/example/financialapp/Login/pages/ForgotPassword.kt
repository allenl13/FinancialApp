package com.example.financialapp.Login.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.financialapp.Login.AuthViewModel
import com.example.financialapp.Login.ResetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun ForgotPassword(
    vm: AuthViewModel,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    val resetState by vm.resetState.observeAsState(ResetState.Idle)
    val snackbarHostState = remember { SnackbarHostState() }

    // React to state changes
    LaunchedEffect(resetState) {
        when (resetState) {
            is ResetState.Error -> {
                snackbarHostState.showSnackbar((resetState as ResetState.Error).message)
            }
            ResetState.Sent -> {
                snackbarHostState.showSnackbar("Password reset email sent. Check your inbox.")
                onBackToLogin()
                vm.clearResetState()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    vm.clearResetState()
                    onBackToLogin()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Forgot Password",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Enter the email you used to sign up. We'll send a link to reset your password."
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            val isSending = resetState is ResetState.Sending

            Button(
                onClick = { vm.sendPasswordReset(email.text.trim()) },
                enabled = !isSending,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sending…")
                } else {
                    Text("Send reset link")
                }
            }

            TextButton(
                onClick = {
                    vm.clearResetState()
                    onBackToLogin()
                }
            ) { Text("Back to login") }
        }
    }
}
