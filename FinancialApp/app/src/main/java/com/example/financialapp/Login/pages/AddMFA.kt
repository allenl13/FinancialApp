package com.example.financialapp.Login.pages


import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financialapp.Login.AuthViewModel

@Composable
fun AddMFAPage(
    vm: AuthViewModel,
    activity: Activity,
    onDone: () -> Unit
) {
    var phone by remember { mutableStateOf("+64") } // nz region by default
    var code by remember { mutableStateOf("") }
    val mfaState by vm.mfaState.observeAsState("")

    LaunchedEffect(mfaState) {
        if (mfaState == "ENROLL_SUCCESS") onDone()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add SMS 2FA")
        Spacer(Modifier.height(16.dp))

        if (mfaState != "ENROLL_CODE_SENT") {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone (+E164)") },
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = { vm.startMfaEnrollment(phone, activity) }) {
                Text(if (mfaState == "ENROLL_SENDING") "Sending..." else "Send Code")
            }
        } else {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("6-digit code") },
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = { vm.finalizeMfaEnrollment(code) }) {
                Text("Verify & Enroll")
            }
        }

        if (mfaState.startsWith("MFA_ERROR")) {
            Spacer(Modifier.height(8.dp))
            Text(mfaState, color = MaterialTheme.colorScheme.error)
        }
    }
}
