package com.example.financialapp.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.financialapp.data.DatabaseModule
import com.example.financialapp.notifications.GoalNotifications
import java.util.concurrent.TimeUnit
import kotlin.math.max

class NotifyGoalReachedWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val id = inputData.getLong("goalId", -1L)
        if (id <= 0L) return Result.success()
        val dao = DatabaseModule.db(applicationContext).savingGoalDao()
        val goal = dao.getById(id) ?: return Result.success()

        if (goal.targetAmount > 0 && goal.savedAmount >= goal.targetAmount) {
            GoalNotifications.notifyGoalReached(applicationContext, goal)
        }
        return Result.success()
    }

    companion object {
        fun enqueue(context: Context, goalId: Long) {
            val req = OneTimeWorkRequestBuilder<NotifyGoalReachedWorker>()
                .setInputData(workDataOf("goalId" to goalId))
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork("goal-reached-$goalId", ExistingWorkPolicy.REPLACE, req)
        }
    }
}

class DeadlineReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val id = inputData.getLong("goalId", -1L)
        if (id <= 0L) return Result.success()
        val dao = DatabaseModule.db(applicationContext).savingGoalDao()
        val goal = dao.getById(id) ?: return Result.success()

        if (goal.dueDate != null && goal.savedAmount < goal.targetAmount) {
            GoalNotifications.notifyDeadlineNear(applicationContext, goal)
        }
        return Result.success()
    }

    companion object {
        private const val DAYS_BEFORE = 3L   // "near deadline" window

        fun schedule(context: Context, goalId: Long, dueAtMillis: Long) {
            val triggerAt = dueAtMillis - TimeUnit.DAYS.toMillis(DAYS_BEFORE)
            val delayMs = max(0L, triggerAt - System.currentTimeMillis())
            val req = OneTimeWorkRequestBuilder<DeadlineReminderWorker>()
                .setInputData(workDataOf("goalId" to goalId))
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork("goal-deadline-$goalId", ExistingWorkPolicy.REPLACE, req)
        }

        fun cancel(context: Context, goalId: Long) {
            WorkManager.getInstance(context).cancelUniqueWork("goal-deadline-$goalId")
        }
    }
}
