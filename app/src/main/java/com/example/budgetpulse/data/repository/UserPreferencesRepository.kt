package com.example.budgetpulse.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.budgetpulse.data.model.AppCurrency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/** Everything the user chooses in Setup / Settings, saved with Jetpack DataStore. */
data class UserPreferences(
    val onboardingComplete: Boolean = false,
    val nickname: String = "",
    val profileImageUri: String? = null,
    val isDarkMode: Boolean = false,
    val fontScale: Float = 1.0f,
    val currency: AppCurrency = AppCurrency.MYR
)

// One DataStore file per app process: /data/data/<package>/files/datastore/user_preferences.preferences_pb
private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferencesRepository(private val context: Context) {

    private val dataStore = context.userPreferencesDataStore

    private object Keys {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val NICKNAME = stringPreferencesKey("nickname")
        val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val CURRENCY = stringPreferencesKey("currency")
    }

    /** Reading: emits every time a preference changes. */
    val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences, using defaults", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            UserPreferences(
                onboardingComplete = prefs[Keys.ONBOARDING_COMPLETE] ?: false,
                nickname = prefs[Keys.NICKNAME] ?: "",
                profileImageUri = prefs[Keys.PROFILE_IMAGE_URI],
                isDarkMode = prefs[Keys.DARK_MODE] ?: false,
                fontScale = prefs[Keys.FONT_SCALE] ?: 1.0f,
                currency = prefs[Keys.CURRENCY]
                    ?.let { name -> AppCurrency.entries.firstOrNull { it.name == name } }
                    ?: AppCurrency.MYR
            )
        }

    // ---------- Writing / updating ----------

    suspend fun completeOnboarding(nickname: String, currency: AppCurrency) {
        dataStore.edit { prefs ->
            prefs[Keys.NICKNAME] = nickname.trim()
            prefs[Keys.CURRENCY] = currency.name
            prefs[Keys.ONBOARDING_COMPLETE] = true
        }
        Log.i(TAG, "Onboarding completed for '$nickname' with currency ${currency.name}")
    }

    suspend fun resetOnboarding() {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = false }
        Log.i(TAG, "Onboarding reset, user will be redirected to Setup")
    }

    suspend fun updateNickname(nickname: String) {
        dataStore.edit { it[Keys.NICKNAME] = nickname.trim() }
        Log.d(TAG, "Nickname updated to '$nickname'")
    }

    suspend fun updateProfileImage(uri: Uri?) {
        if (uri != null) {
            // Keep read access to the picked photo after the app restarts
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: SecurityException) {
                Log.w(TAG, "Could not persist permission for $uri", e)
            }
        }
        dataStore.edit { prefs ->
            if (uri == null) prefs.remove(Keys.PROFILE_IMAGE_URI) else prefs[Keys.PROFILE_IMAGE_URI] = uri.toString()
        }
        Log.d(TAG, "Profile image updated: $uri")
    }

    suspend fun updateDarkMode(isDark: Boolean) {
        dataStore.edit { it[Keys.DARK_MODE] = isDark }
        Log.d(TAG, "Dark mode set to $isDark")
    }

    suspend fun updateFontScale(scale: Float) {
        dataStore.edit { it[Keys.FONT_SCALE] = scale }
        Log.d(TAG, "Font scale set to $scale")
    }

    suspend fun updateCurrency(currency: AppCurrency) {
        dataStore.edit { it[Keys.CURRENCY] = currency.name }
        Log.d(TAG, "Currency set to ${currency.name}")
    }

    companion object {
        private const val TAG = "BudgetPulseDataStore"
    }
}