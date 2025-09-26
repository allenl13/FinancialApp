// com/example/financialapp/subscriptions/alerts/DueSubscriptionsWorker.kt
package com.example.financialapp.subscriptions.alerts

import android.Manifest
import android.R
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.financialapp.MainActivity
import com.example.financialapp.notifications.SubsNotifications
import com.example.financialapp.subscriptions.SubDatabase
import com.example.financialapp.subscriptions.SubEntity
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DueChecker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val dao = SubDatabase.getDatabase(appContext).subDao()
    private val prefs = appContext.getSharedPreferences("subs_alerts", Context.MODE_PRIVATE)
    @RequiresApi(Build.VERSION_CODES.O)
    private val zone: ZoneId = ZoneId.systemDefault()
    @RequiresApi(Build.VERSION_CODES.O)
    private val iso = DateTimeFormatter.ISO_LOCAL_DATE
    @RequiresApi(Build.VERSION_CODES.O)
    private val displayDate = DateTimeFormatter.ofPattern("dd MMM yyyy")

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        SubsNotifications.ensureChannel(applicationContext)

        val now = ZonedDateTime.now(zone)
        val todayEpochDay = now.toLocalDate().toEpochDay()

        val subs = dao.getAllNow()
        for (s in subs) {
            if (!s.isActive) continue

            val lead = s.reminder.coerceIn(0, 2)
            val dueLocal = parseDate(s.dueDate) ?: continue
            val due = dueLocal.atStartOfDay(zone)

            val withinWindow =
                !due.isBefore(now) &&
                        now.plusDays(lead.toLong()).toLocalDate() >= dueLocal

            val key = "lastAlertDay_${s.id}"
            val last = prefs.getLong(key, Long.MIN_VALUE)
            val notAlertedToday = last != todayEpochDay

            if (withinWindow && notAlertedToday) {
                notifyDue(s, dueLocal)
                prefs.edit().putLong(key, todayEpochDay).apply()
            }
        }
        return Result.success()
    }

    private fun formatAmount(cents: Int): String {
        val dollars = cents / 100.0
        return "$" + String.format("%.2f", dollars)
    }

        @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun notifyDue(sub: SubEntity, due: LocalDate) {
        val ctx = applicationContext
        val intent = Intent(ctx, MainActivity::class.java).apply {
            putExtra("nav", "sub")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pi = PendingIntent.getActivity(
            ctx, sub.id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val n = NotificationCompat.Builder(ctx, SubsNotifications.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("Subscription Due: ${sub.name}")
            .setContentText("Pay ${formatAmount(sub.amount)} by ${due.format(displayDate)}")
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()

        NotificationManagerCompat.from(ctx).notify(sub.id.hashCode(), n)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDate(s: String): LocalDate? =
        runCatching { LocalDate.parse(s, iso) }.getOrNull()
            ?: runCatching { LocalDate.parse(s, displayDate) }.getOrNull()
}