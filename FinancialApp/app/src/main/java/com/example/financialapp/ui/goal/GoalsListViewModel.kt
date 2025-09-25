    package com.example.financialapp.ui.goal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialapp.data.DatabaseModule
import com.example.financialapp.data.goal.SavingGoal
import com.example.financialapp.data.goal.SavingGoalRepository
import com.example.financialapp.work.DeadlineReminderWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import com.example.financialapp.util.DMY
import kotlin.math.min

data class GoalListItemUi(
    val id: Long,
    val name: String,
    val saved: Double,
    val target: Double,
    val progress: Float,
    val dueDateMillis: Long?
)

class GoalsListViewModel(app: Application) : AndroidViewModel(app) {
    private val repo by lazy { SavingGoalRepository(DatabaseModule.db(app).savingGoalDao()) }

    val goals: StateFlow<List<GoalListItemUi>> =
        repo.observeAll()
            .map { list -> list.map { it.toUi() } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun create(name: String, targetText: String, dueText: String?, categoryId: Long?) =
        viewModelScope.launch {
            val target = targetText.toDoubleOrNull() ?: 0.0
            val due = dueText?.toEpochDmyOrNull()
            if (name.isNotBlank() && target > 0) {
                // repo.create returns the inserted row id (Long)
                val id = repo.create(name.trim(), target, due, categoryId)
                if (due != null) {
                    // schedule a reminder ~DAYS_BEFORE the due date
                    DeadlineReminderWorker.schedule(getApplication(), id, due)
                }
            }
        }

    private fun SavingGoal.toUi() = GoalListItemUi(
        id = id,
        name = name,
        saved = savedAmount,
        target = targetAmount,
        progress = if (targetAmount <= 0) 0f else min(1f, (savedAmount / targetAmount).toFloat()),
        dueDateMillis = dueDate
    )
}

/** Accepts DD/MM/YYYY and returns epoch millis or null. */
private fun String.toEpochDmyOrNull(): Long? = runCatching {
    LocalDate.parse(this.trim(), DMY)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}.getOrNull()
