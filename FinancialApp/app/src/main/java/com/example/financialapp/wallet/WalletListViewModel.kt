package com.example.financialapp.wallet

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WalletListViewModel : ViewModel() {

    private val _card = MutableStateFlow<List<WalletCard>>(emptyList())
    val card: StateFlow<List<WalletCard>> = _card

    fun create(
        name: String,
        network: String,
        last4: String,
        expire: String
    ){
        val item = WalletCard(
            id = System.currentTimeMillis(),
            name = name.trim(),
            network = network.trim(),
            last4 = last4.takeLast(4),
            expire = expire.trim()
        )
        _card.value = _card.value + item

        // TODO: ADD ROOM DB FOR SAVING CARD
    }
}