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

/** Sort modes for the goals list. */
enum class GoalSort { DUE_DATE, REMAINING, CATEGORY }

class GoalsListViewModel(app: Application) : AndroidViewModel(app) {
    private val repo by lazy { SavingGoalRepository(DatabaseModule.db(app).savingGoalDao()) }
    private val catDao by lazy { DatabaseModule.db(app).savingCategoryDao() }

    // --- Sort state ---
    private val _sortMode = MutableStateFlow(GoalSort.DUE_DATE)
    val sortMode: StateFlow<GoalSort> = _sortMode
    fun setSort(mode: GoalSort) { _sortMode.value = mode }

    // --- Search state ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    fun setSearch(q: String) { _searchQuery.value = q }
    fun clearSearch() { _searchQuery.value = "" }

    // --- Category filter state (null = All) ---
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId
    fun setCategoryFilter(id: Long?) { _selectedCategoryId.value = id }

    // --- Categories (for labels & sorting by category name) ---
    val categories: StateFlow<List<CategoryMiniUi>> =
        catDao.observeAll()
            .map { list -> list.map { it.toMiniUi() } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val categoryMap: StateFlow<Map<Long, CategoryMiniUi>> =
        categories.map { it.associateBy { c -> c.id } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    // --- Goals stream: search -> category filter -> sort (uses catMap when needed) ---
    val goals: StateFlow<List<GoalListItemUi>> =
        repo.observeAll()
            .map { list -> list.map { it.toUi() } }
            .combine(_searchQuery) { list, q ->
                val query = q.trim()
                if (query.isBlank()) list
                else list.filter { it.name.contains(query, ignoreCase = true) }
            }
            .combine(_selectedCategoryId) { list, catId ->
                if (catId == null) list else list.filter { it.categoryId == catId }
            }
            .combine(categoryMap) { list, catMap -> list to catMap }
            .combine(_sortMode) { (list, catMap), mode ->
                when (mode) {
                    GoalSort.DUE_DATE -> list.sortedWith(
                        compareBy<GoalListItemUi> { it.dueDateMillis == null }
                            .thenBy { it.dueDateMillis ?: Long.MAX_VALUE }
                            .thenBy { it.name.lowercase() }
                    )
                    GoalSort.REMAINING -> list.sortedWith(
                        compareBy<GoalListItemUi> { (it.target - it.saved).coerceAtLeast(0.0) }
                            .thenBy { it.name.lowercase() }
                    )
                    GoalSort.CATEGORY -> list.sortedWith(
                        compareBy<GoalListItemUi>(
                            { it.categoryId == null },                                                 // nulls last
                            { catMap[it.categoryId]?.name?.lowercase() ?: "" },                        // by category name
                            { it.name.lowercase() }                                                    // then goal name
                        )
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
