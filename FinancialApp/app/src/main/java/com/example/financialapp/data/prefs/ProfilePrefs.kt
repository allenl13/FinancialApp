package com.example.financialapp.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore by preferencesDataStore(name = "profile_prefs")

class ProfilePrefs(private val context: Context) {

    private val KEY_IMAGE_URI = stringPreferencesKey("profile_image_uri")

    val imageUriFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_IMAGE_URI]
    }

    suspend fun saveImageUri(uri: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IMAGE_URI] = uri
        }
    }
}

