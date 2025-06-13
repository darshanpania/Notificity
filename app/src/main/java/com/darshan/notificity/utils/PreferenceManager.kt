package com.darshan.notificity.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Generic PreferenceManager using DataStore to save and retrieve preferences by key.
 */
object PreferenceManager {
    private val Context.dataStore by preferencesDataStore(
        name = "settings"
    )

    /**
     * Save a String preference by key.
     *
     * @param context Context
     * @param key The key for the preference
     * @param value The string value to save
     */
    suspend fun saveString(context: Context, key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    /**
     * Save an Int preference by key.
     *
     * @param context Context
     * @param key The key for the preference
     * @param value The integer value to save
     */
    suspend fun saveInt(context: Context, key: String, value: Int) {
        val preferencesKey = intPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    /**
     * Get a Flow of String preference by key.
     *
     * @param context Context required for accessing DataStore
     * @param key The key for the preference
     * @param defaultValue The default value if preference is not set
     * @return Flow emitting the preference value
     */
    fun getStringFlow(context: Context, key: String, defaultValue: String): Flow<String> {
        val preferencesKey = stringPreferencesKey(key)
        return context.dataStore.data
            .map { preferences -> preferences[preferencesKey] ?: defaultValue }
    }

    /**
     * Get a Flow of Int preference by key.
     *
     * @param context Context required for accessing DataStore
     * @param key The key for the preference
     * @param defaultValue The default value if preference is not set
     * @return Flow emitting the preference value
     */
    fun getIntFlow(context: Context, key: String, defaultValue: Int): Flow<Int> {
        val preferencesKey = intPreferencesKey(key)
        return context.dataStore.data
            .map { preferences -> preferences[preferencesKey] ?: defaultValue }
    }
}
