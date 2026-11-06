package com.example.fatecskip.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fatecskip.data.preferences.UserPreferencesRepository
import com.example.fatecskip.data.repository.AcademicRepository
import com.example.fatecskip.worker.DailyCheckWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: AcademicRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val studentNickname: StateFlow<String> = preferencesRepository.studentNicknameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Guerreiro da Fatec")

    val selectedSemester: StateFlow<Int> = preferencesRepository.selectedSemesterFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val courageLevel: StateFlow<Float> = preferencesRepository.courageLevelFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.75f)

    val notificationsEnabled: StateFlow<Boolean> = preferencesRepository.notificationsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val apiUrl: StateFlow<String> = preferencesRepository.apiUrlFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferencesRepository.DEFAULT_API_URL)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    fun updateNickname(name: String) {
        viewModelScope.launch {
            preferencesRepository.setStudentNickname(name)
            _feedbackMessage.value = "Apelido atualizado para '$name'!"
        }
    }

    fun updateSemester(semester: Int) {
        viewModelScope.launch {
            preferencesRepository.setSelectedSemester(semester)
            _feedbackMessage.value = "Semestre alterado para o ${semester}º semestre!"
        }
    }

    fun updateCourageLevel(level: Float) {
        viewModelScope.launch {
            preferencesRepository.setCourageLevel(level)
            val description = when {
                level <= 0.6f -> "Modo Nerdola ativado! (Alerta cedo)"
                level <= 0.8f -> "Modo Estudante Médio ativado! (Equilíbrio sono/diploma)"
                else -> "Modo Vida Loka ativado! (Alerta só na beira do abismo)"
            }
            _feedbackMessage.value = description
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
            _feedbackMessage.value = if (enabled) {
                "Notificações ativadas! O Oráculo vai te vigiar."
            } else {
                "Notificações desativadas. Durma por sua conta e risco."
            }
        }
    }

    fun updateApiUrl(url: String) {
        viewModelScope.launch {
            preferencesRepository.setApiUrl(url)
        }
    }

    fun syncFromApi() {
        viewModelScope.launch {
            _isSyncing.value = true
            val currentSemester = selectedSemester.value
            val currentUrl = apiUrl.value
            val result = repository.syncFromApi(currentSemester, currentUrl)
            _isSyncing.value = false

            result.fold(
                onSuccess = { msg -> _feedbackMessage.value = msg },
                onFailure = { err -> _feedbackMessage.value = "Erro na sincronização: ${err.localizedMessage}" }
            )
        }
    }

    fun triggerTestNotification(context: Context) {
        viewModelScope.launch {
            DailyCheckWorker.scheduleTestNotification(context, delaySeconds = 5)
            _feedbackMessage.value = "⚡ Teste agendado! Em 5 segundos o Oráculo enviará uma notificação..."
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            preferencesRepository.setOnboardingCompleted(false)
            _feedbackMessage.value = "Configuração inicial redefinida! Abra o app novamente para vê-la."
        }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    class Factory(
        private val repository: AcademicRepository,
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository, preferencesRepository) as T
        }
    }
}

