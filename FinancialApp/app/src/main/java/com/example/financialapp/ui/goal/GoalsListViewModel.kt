package com.example.financialapp.ui.goal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialapp.data.DatabaseModule
import com.example.financialapp.data.goal.SavingGoal
import com.example.financialapp.data.goal.SavingGoalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

import java.time.LocalDate
import java.time.ZoneId
import com.example.financialapp.util.DMY


data class GoalListItemUi(
    val id: Long,
    val name: String,
    val saved: Double,
    val target: Double,
    val progress: Float, // 0f..1f
    val dueDateMillis: Long?
)

class GoalsListViewModel(app: Application) : AndroidViewModel(app) {
    private val repo by lazy {
        SavingGoalRepository(DatabaseModule.db(app).savingGoalDao())
    }

    val goals: StateFlow<List<GoalListItemUi>> =
        repo.observeAll()
            .map { list -> list.map { it.toUi() } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun create(name: String, targetText: String, dueText: String?, categoryId: Long?) = viewModelScope.launch {
        val target = targetText.toDoubleOrNull() ?: 0.0
        val due = dueText?.toEpochDmyOrNull()
        if (name.isNotBlank() && target > 0) {
            repo.create(name, target, due, categoryId)
        }
    }

    fun delete(id: Long) = viewModelScope.launch { repo.delete(id) }

    private fun SavingGoal.toUi() = GoalListItemUi(
        id = id,
        name = name,
        saved = savedAmount,
        target = targetAmount,
        progress = if (targetAmount <= 0) 0f else min(1f, (savedAmount / targetAmount).toFloat()),
        dueDateMillis = dueDate
    )
}

private fun String.toEpochDmyOrNull(): Long? = runCatching {
    val fmt = java.time.format.DateTimeFormatter
        .ofPattern("dd/MM/uuuu")
        .withResolverStyle(java.time.format.ResolverStyle.STRICT)
    java.time.LocalDate.parse(this.trim(), fmt)
        .atStartOfDay(java.time.ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}.getOrNull()
