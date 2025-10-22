package com.example.financialapp.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun EnsureNotificationsReady() {
    val ctx = LocalContext.current

    // Always make sure the channel exists
    LaunchedEffect(Unit) { GoalNotifications.ensureChannel(ctx) }

    // Request POST_NOTIFICATIONS on Android 13+
    if (Build.VERSION.SDK_INT >= 33) {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { /* no-op */ }
        )
        val granted = ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED
        LaunchedEffect(granted) {
            if (!granted) launcher.launch(permission)
        }
    }
}