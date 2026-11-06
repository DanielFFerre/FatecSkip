package com.example.fatecskip.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val KEY_SELECTED_SEMESTER = intPreferencesKey("selected_semester")
        val KEY_COURAGE_LEVEL = floatPreferencesKey("courage_level")
        val KEY_STUDENT_NICKNAME = stringPreferencesKey("student_nickname")
        val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val KEY_API_URL = stringPreferencesKey("api_url")

        const val DEFAULT_SEMESTER = 1
        const val DEFAULT_COURAGE_LEVEL = 0.75f // Estudante Médio
        const val DEFAULT_NICKNAME = "Guerreiro da Fatec"
        const val DEFAULT_API_URL = "https://raw.githubusercontent.com/antigravity/fatecskip/main/fatec_api.json"
    }

    val selectedSemesterFlow: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_SELECTED_SEMESTER] ?: DEFAULT_SEMESTER
        }

    val courageLevelFlow: Flow<Float> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_COURAGE_LEVEL] ?: DEFAULT_COURAGE_LEVEL
        }

    val studentNicknameFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_STUDENT_NICKNAME] ?: DEFAULT_NICKNAME
        }

    val notificationsEnabledFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] ?: true
        }

    val apiUrlFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_API_URL] ?: DEFAULT_API_URL
        }

    suspend fun setSelectedSemester(semester: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_SELECTED_SEMESTER] = semester
        }
    }

    suspend fun setCourageLevel(level: Float) {
        dataStore.edit { preferences ->
            preferences[KEY_COURAGE_LEVEL] = level
        }
    }

    suspend fun setStudentNickname(nickname: String) {
        dataStore.edit { preferences ->
            preferences[KEY_STUDENT_NICKNAME] = nickname.ifBlank { DEFAULT_NICKNAME }
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setApiUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[KEY_API_URL] = url
        }
    }
}

