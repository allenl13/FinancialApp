package com.example.financialapp.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.bgDataStore by preferencesDataStore(name = "background_fixed_prefs")

class BackgroundPrefs(private val context: Context) {
    private val KEY_BG_RES_ID = intPreferencesKey("bg_res_id")


    val selectedResIdFlow: Flow<Int?> =
        context.bgDataStore.data.map { prefs -> prefs[KEY_BG_RES_ID] }


    suspend fun save(resId: Int) {
        context.bgDataStore.edit { prefs -> prefs[KEY_BG_RES_ID] = resId }
    }


    suspend fun clear() {
        context.bgDataStore.edit { prefs -> prefs.remove(KEY_BG_RES_ID) }
    }
}