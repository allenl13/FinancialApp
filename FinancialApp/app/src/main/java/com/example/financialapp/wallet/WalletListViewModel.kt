package com.example.financialapp.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WalletListViewModel : ViewModel() {
    private val _cards = MutableStateFlow<List<WalletCard>>(emptyList())
    val cards: StateFlow<List<WalletCard>> = _cards

    fun create(name: String, network: String, last4: String, expire: String) = viewModelScope.launch {
        val item = WalletCard(
            id = System.currentTimeMillis(),
            name = name.trim(),
            network = network.trim(),
            last4 = last4.takeLast(4),
            expire = expire.trim()
        )
        _cards.value = _cards.value + item

        // TODO: ADD ROOM DB FOR SAVING CARD
    }

    fun update(id: Long, name: String, network: String, last4: String, expire: String) = viewModelScope.launch {
        _cards.value = _cards.value.map { c ->
            if (c.id == id) c.copy(name = name.trim(), network = network.trim(),
                last4 = last4.takeLast(4), expire = expire.trim())
            else c

            // TODO: ADD ROOM DB FOR SAVING CARD
        }
    }

    fun delete(id: Long) { _cards.value = _cards.value.filterNot { it.id == id }
        // TODO: ADD ROOM DB FOR SAVING CARD
    }

    fun observeById(id: Long): StateFlow<WalletCard?> =
        _cards.map { list -> list.firstOrNull { it.id == id } }
            .stateIn(viewModelScope, SharingStarted.Lazily, null)
}