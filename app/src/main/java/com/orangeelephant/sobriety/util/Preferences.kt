package com.orangeelephant.sobriety.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.orangeelephant.sobriety.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFERENCE_FILE = "com.orangeelephant.sobriety_preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCE_FILE)

class SobrietyPreferences(context: Context) {
    private val preferenceDataStore = context.dataStore
    companion object {
        const val DYNAMIC_COLOURS = "dynamic_colours"
        const val THEME = "theme"
        const val LANGUAGE = "language"

        const val BIOMETRIC_UNLOCK = "biometric_unlock"
        const val ENCRYPTED_BY_PASSWORD = "encrypted_by_password"
        const val PASSWORD_SALT = "password_salt"
    }

    val dynamicColours: Flow<Boolean> = getPrefFlow(booleanPreferencesKey(DYNAMIC_COLOURS), true)
    val theme: Flow<String> = getPrefFlow(stringPreferencesKey(THEME), "default")
    val language: Flow<String> = getPrefFlow(stringPreferencesKey(LANGUAGE), "default")

    val biometricUnlock: Flow<Boolean> = getPrefFlow(booleanPreferencesKey(BIOMETRIC_UNLOCK), false)
    val encryptedByPassword: Flow<Boolean> = getPrefFlow(booleanPreferencesKey(ENCRYPTED_BY_PASSWORD), false)
    val passwordSalt: Flow<String> = getPrefFlow(stringPreferencesKey(PASSWORD_SALT), "")

    suspend fun setEncryptedByPassword(value: Boolean) {
        editPref(booleanPreferencesKey(ENCRYPTED_BY_PASSWORD), value)
    }

    suspend fun setPasswordSalt(value: String) {
        editPref(stringPreferencesKey(PASSWORD_SALT), value)
    }

    private fun <T> getPrefFlow(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return preferenceDataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    private suspend fun <T> editPref(key: Preferences.Key<T>, value: T) {
        preferenceDataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    private val _availableLanguages = mapOf(
        "default" to context.getString(R.string.system_default),
        "en" to "English"
    )
    val availableLanguages: Map<String, String>
        get() = _availableLanguages

    private val _availableThemes = mapOf(
        "default" to context.getString(R.string.system_default),
        "light" to context.getString(R.string.light_mode),
        "dark" to context.getString(R.string.dark_mode)
    )
    val availableThemes: Map<String, String>
        get() = _availableThemes
}