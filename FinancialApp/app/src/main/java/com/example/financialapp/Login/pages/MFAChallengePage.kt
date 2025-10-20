package com.example.financialapp.Login.pages

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financialapp.Login.AuthViewModel

@Composable
fun MFAChallengePage(
    vm: AuthViewModel,
    activity: Activity,
    onSuccess: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    val mfaState by vm.mfaState.observeAsState("")

    LaunchedEffect(Unit) {
        // triggers sending the SMS for the enrolled factor
        vm.startMfaChallenge(activity)
    }
    LaunchedEffect(mfaState) {
        if (mfaState == "MFA_SUCCESS") onSuccess()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter the verification code")
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("6-digit code") })
        Spacer(Modifier.height(12.dp))
        Button(onClick = { vm.verifyMfaCode(code) }) { Text("Verify") }

        if (mfaState.startsWith("MFA_ERROR")) {
            Spacer(Modifier.height(8.dp))
            Text(mfaState, color = MaterialTheme.colorScheme.error)
        }
    }
}
