package com.example.financialapp.ui.goal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialapp.data.DatabaseModule
import com.example.financialapp.data.category.SavingCategory
import com.example.financialapp.data.goal.SavingGoal
import com.example.financialapp.data.goal.SavingGoalRepository
import com.example.financialapp.work.DeadlineReminderWorker
import kotlinx.coroutines.flow.*
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
    val dueDateMillis: Long?,
    val categoryId: Long?
)

data class CategoryMiniUi(
    val id: Long,
    val name: String,
    val colorHex: String
)

class GoalsListViewModel(app: Application) : AndroidViewModel(app) {
    private val repo by lazy { SavingGoalRepository(DatabaseModule.db(app).savingGoalDao()) }
    private val catDao by lazy { DatabaseModule.db(app).savingCategoryDao() }

    val goals: StateFlow<List<GoalListItemUi>> =
        repo.observeAll()
            .map { list -> list.map { it.toUi() } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val categories: StateFlow<List<CategoryMiniUi>> =
        catDao.observeAll()
            .map { list -> list.map { it.toMiniUi() } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val categoryMap: StateFlow<Map<Long, CategoryMiniUi>> =
        categories.map { it.associateBy { c -> c.id } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    fun create(name: String, targetText: String, dueText: String?, categoryId: Long?) =
        viewModelScope.launch {
            val target = targetText.toDoubleOrNull() ?: 0.0
            val due = dueText?.toEpochDmyOrNull()
            if (name.isNotBlank() && target > 0) {
                val id = repo.create(name.trim(), target, due, categoryId)
                if (due != null) DeadlineReminderWorker.schedule(getApplication(), id, due)
            }
        }

    /** Create a category inline from the Add Goal dialog. Returns inserted id or null. */
    suspend fun createCategory(name: String, colorHex: String): Long? {
        if (name.isBlank()) return null
        val id = catDao.upsert(SavingCategory(name = name.trim(), colorHex = colorHex, goalAmount = 0.0))
        return id.takeIf { it > 0 }
    }

    private fun SavingGoal.toUi() = GoalListItemUi(
        id = id,
        name = name,
        saved = savedAmount,
        target = targetAmount,
        progress = if (targetAmount <= 0) 0f else min(1f, (savedAmount / targetAmount).toFloat()),
        dueDateMillis = dueDate,
        categoryId = categoryId
    )

    private fun SavingCategory.toMiniUi() = CategoryMiniUi(
        id = id, name = name, colorHex = colorHex
    )
}

private fun String.toEpochDmyOrNull(): Long? = runCatching {
    LocalDate.parse(this.trim(), DMY)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}.getOrNull()
