package com.dev.lokabudaya.pages.Profile.Menu.Notification

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationDataStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("notification_settings")
        val APP_NOTIFICATION_ENABLED = booleanPreferencesKey("app_notification_enabled")
    }

    // Flow untuk membaca status notifikasi
    val notificationEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[APP_NOTIFICATION_ENABLED] ?: true // Default true
        }

    // Fungsi untuk menyimpan status notifikasi
    suspend fun saveNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[APP_NOTIFICATION_ENABLED] = enabled
        }
    }
}
