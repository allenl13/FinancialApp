package com.example.financialapp.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialapp.data.DatabaseModule
import com.example.financialapp.data.category.SavingCategory
import com.example.financialapp.data.category.SavingCategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategoryUi(
    val id: Long,
    val name: String,
    val colorHex: String
)

class SavingCategoryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo by lazy {
        val dao = DatabaseModule.db(app).savingCategoryDao()
        SavingCategoryRepository(dao)
    }

    val categories: StateFlow<List<CategoryUi>> =
        repo.observeAll()
            .map { list -> list.map { it.toUi() } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /** Create a category without any goal amount. */
    fun create(name: String, colorHex: String) = viewModelScope.launch {
        if (name.isNotBlank()) repo.create(name.trim(), colorHex)
    }

    private fun SavingCategory.toUi() = CategoryUi(
        id = id, name = name, colorHex = colorHex
    )
}
