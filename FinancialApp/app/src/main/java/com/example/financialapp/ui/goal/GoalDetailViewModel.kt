package com.example.financialapp.ui.goal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.financialapp.data.DatabaseModule
import com.example.financialapp.data.goal.SavingGoal
import com.example.financialapp.data.goal.SavingGoalRepository
import com.example.financialapp.util.DMY
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.min

data class GoalDetailUi(
    val id: Long,
    val name: String,
    val saved: Double,
    val target: Double,
    val progress: Float,
    val createdAt: Long,
    val dueDate: Long?,
    val onTrack: Boolean
)

class GoalDetailViewModel(
    app: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(app) {

    private val repo = SavingGoalRepository(DatabaseModule.db(app).savingGoalDao())
    private val goalId: Long = checkNotNull(savedStateHandle["goalId"])

    val goal: StateFlow<GoalDetailUi?> =
        repo.observeById(goalId)
            .map { it?.toUi() }
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun updateSaved(newSavedText: String) = viewModelScope.launch {
        newSavedText.toDoubleOrNull()?.let { repo.updateSaved(goalId, it) }
    }

    /** Bulk edit from Edit dialog: name, target, due (DD/MM/YYYY) with optional clear-due. */
    fun updateAll(nameText: String, targetText: String, dueText: String?, clearDue: Boolean) =
        viewModelScope.launch {
            val name = nameText.trim().takeIf { it.isNotEmpty() }
            val target = targetText.toDoubleOrNull()
            val due = when {
                clearDue -> null
                dueText.isNullOrBlank() -> null
                else -> dueText.toEpochDmyOrNull()
            }
            repo.updateAll(goalId, name, target, due, clearDue)
        }

    fun delete() = viewModelScope.launch { repo.delete(goalId) }

    private fun SavingGoal.toUi(): GoalDetailUi {
        val p = if (targetAmount <= 0) 0f else min(1f, (savedAmount / targetAmount).toFloat())
        return GoalDetailUi(
            id = id,
            name = name,
            saved = savedAmount,
            target = targetAmount,
            progress = p,
            createdAt = createdAt,
            dueDate = dueDate,
            onTrack = isOnTrack(System.currentTimeMillis())
        )
    }

    /** On-track if saved so far >= expected by linear pace between createdAt and dueDate. */
    private fun SavingGoal.isOnTrack(now: Long): Boolean {
        val end = dueDate ?: return true
        if (end <= createdAt) return true
        val total = (end - createdAt).toDouble()
        val elapsed = (now - createdAt).coerceIn(0, end - createdAt).toDouble()
        val expected = if (total <= 0) 0.0 else (targetAmount * (elapsed / total))
        return savedAmount + 1e-6 >= expected
    }
}

/** Parse "DD/MM/YYYY" into epoch millis, or null if invalid. */
private fun String.toEpochDmyOrNull(): Long? = runCatching {
    LocalDate.parse(this.trim(), DMY)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}.getOrNull()
