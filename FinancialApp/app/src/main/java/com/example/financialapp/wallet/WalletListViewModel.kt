package com.example.financialapp.wallet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WalletListViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = WalletDB.getDatabase(application).walletCardDao()
    private val _cards = MutableStateFlow<List<WalletCard>>(emptyList())
    val cards: StateFlow<List<WalletCard>> = _cards

    init {
        viewModelScope.launch {
            _cards.value = dao.getAll().map { it.toModel() }
        }
    }

    fun create(name: String, network: String, last4: String, expire: String) = viewModelScope.launch {
        val item = WalletEntity(name = name, network = network, last4 = last4.takeLast(4), expire = expire)
        dao.insert(item)
        _cards.value = dao.getAll().map { it.toModel() }
    }

    fun update(id: Long, name: String, network: String, last4: String, expire: String) = viewModelScope.launch {
        val updated = WalletEntity(id, name, network, last4.takeLast(4), expire)
        dao.update(updated)
        _cards.value = dao.getAll().map { it.toModel() }
    }

    fun delete(id: Long) = viewModelScope.launch {
        dao.getById(id)?.let { dao.delete(it) }
        _cards.value = dao.getAll().map { it.toModel() }
    }

    fun WalletEntity.toModel() = WalletCard(id, name, network, last4, expire)

    fun observeById(id: Long): Flow<WalletCard?> {
        return dao.observeById(id).map { it?.toModel() }
    }
}