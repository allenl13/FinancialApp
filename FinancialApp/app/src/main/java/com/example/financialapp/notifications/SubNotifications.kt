package com.example.financialapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object SubsNotifications {
    const val CHANNEL_ID = "subs_due"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(NotificationManager::class.java)
            val ch = NotificationChannel(
                CHANNEL_ID,
                "Subscription Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Alerts before subscriptions are due" }
            mgr.createNotificationChannel(ch)
        }
    }
}
