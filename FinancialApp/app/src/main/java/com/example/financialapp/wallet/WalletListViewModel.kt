package com.example.financialapp.wallet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WalletListViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = WalletDB.getDatabase(application).walletCardDao()

    private val _cards = MutableStateFlow<List<WalletCard>>(emptyList())
    val cards: StateFlow<List<WalletCard>> = _cards

    init {
        // Initial load from DB
        viewModelScope.launch { refresh() }
    }

    private suspend fun refresh() {
        _cards.value = dao.getAll().map { it.toModel() }
    }

    fun create(
        name: String,
        network: String,
        last4: String,
        expire: String
    ) = viewModelScope.launch {
        // basic guardrails like the other branch had
        val cleanName = name.trim()
        val cleanNetwork = network.trim()
        val cleanExpire = expire.trim()
        val cleanLast4 = last4.trim().takeLast(4)

        if (cleanName.isBlank() || cleanLast4.isBlank()) return@launch

        val entity = WalletEntity(
            name = cleanName,
            network = cleanNetwork,
            last4 = cleanLast4,
            expire = cleanExpire
        )
        dao.insert(entity)
        refresh()
    }

    fun update(
        id: Long,
        name: String,
        network: String,
        last4: String,
        expire: String
    ) = viewModelScope.launch {
        val cleanName = name.trim()
        val cleanNetwork = network.trim()
        val cleanExpire = expire.trim()
        val cleanLast4 = last4.trim().takeLast(4)

        if (cleanName.isBlank() || cleanLast4.isBlank()) return@launch

        val updated = WalletEntity(
            id = id,
            name = cleanName,
            network = cleanNetwork,
            last4 = cleanLast4,
            expire = cleanExpire
        )
        dao.update(updated)
        refresh()
    }

    fun delete(id: Long) = viewModelScope.launch {
        dao.getById(id)?.let { dao.delete(it) }
        refresh()
    }

    // Mapping extension
    private fun WalletEntity.toModel() = WalletCard(id, name, network, last4, expire)

    // Stream a single card by id (useful for detail screen with collectAsState)
    fun observeById(id: Long): Flow<WalletCard?> {
        return dao.observeById(id).map { it?.toModel() }
    }
}