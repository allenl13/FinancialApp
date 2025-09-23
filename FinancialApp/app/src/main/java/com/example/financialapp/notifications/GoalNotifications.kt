package com.example.financialapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.financialapp.data.goal.SavingGoal

object GoalNotifications {
    const val CHANNEL_ID = "goals"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(NotificationManager::class.java)
            val ch = NotificationChannel(
                CHANNEL_ID, "Saving Goals", NotificationManager.IMPORTANCE_DEFAULT
            )
            mgr.createNotificationChannel(ch)
        }
    }

    private fun amount(v: Double) = "$" + String.format("%.2f", v)

    fun notifyGoalReached(context: Context, goal: SavingGoal) {
        ensureChannel(context)
        val text = "${goal.name}: ${amount(goal.savedAmount)} / ${amount(goal.targetAmount)}"
        val n = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Goal reached 🎉")
            .setContentText(text)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context)
            .notify(("goal-reached-${goal.id}").hashCode(), n)
    }

    fun notifyDeadlineNear(context: Context, goal: SavingGoal) {
        ensureChannel(context)
        val n = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Goal due soon")
            .setContentText("${goal.name} is nearing its deadline.")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context)
            .notify(("goal-deadline-${goal.id}").hashCode(), n)
    }
}
