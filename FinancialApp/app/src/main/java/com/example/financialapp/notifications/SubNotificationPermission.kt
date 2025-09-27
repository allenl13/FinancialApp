package com.example.financialapp.notifications

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.financialapp.subscriptions.alerts.DueChecker
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EnsureSubNotificationsReady() {
    val ctx = LocalContext.current

    LaunchedEffect(Unit) {
        SubsNotifications.ensureChannel(ctx)

        scheduleDailyDueCheck(ctx.applicationContext)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun scheduleDailyDueCheck(context: Context) {
    val now = ZonedDateTime.now()
    val target = now.withHour(9).withMinute(0).withSecond(0).withNano(0)
        .let { if (it.isBefore(now)) it.plusDays(1) else it }
    val delayMin = Duration.between(now, target).toMinutes().coerceAtLeast(1)

    val req = PeriodicWorkRequestBuilder<DueChecker>(
        1, TimeUnit.DAYS
    )
        .setInitialDelay(delayMin, TimeUnit.MINUTES)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "subs_due_check",
        ExistingPeriodicWorkPolicy.UPDATE,
        req
    )
}
