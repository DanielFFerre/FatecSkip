package com.example.fatecskip.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fatecskip.data.local.entities.SubjectEntity
import com.example.fatecskip.data.preferences.UserPreferencesRepository
import com.example.fatecskip.data.repository.AcademicRepository
import com.example.fatecskip.domain.model.SkipVerdict
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val repository: AcademicRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val studentNickname: StateFlow<String> = preferencesRepository.studentNicknameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Guerreiro da Fatec")

    val selectedSemester: StateFlow<Int> = preferencesRepository.selectedSemesterFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val courageLevel: StateFlow<Float> = preferencesRepository.courageLevelFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.75f)

    // Lista reativa de matérias para o semestre atual
    val subjects: StateFlow<List<SubjectEntity>> = selectedSemester
        .flatMapLatest { semester ->
            repository.getSubjectsForSemester(semester)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _verdict = MutableStateFlow<SkipVerdict?>(null)
    val verdict: StateFlow<SkipVerdict?> = _verdict.asStateFlow()

    private val _isEvaluating = MutableStateFlow(false)
    val isEvaluating: StateFlow<Boolean> = _isEvaluating.asStateFlow()

    private val _userFeedback = MutableStateFlow<String?>(null)
    val userFeedback: StateFlow<String?> = _userFeedback.asStateFlow()

    fun consultOracle() {
        viewModelScope.launch {
            _isEvaluating.value = true
            // Suspense cômico do Oráculo calculando
            delay(500)
            val result = repository.evaluateSkipVerdict(
                date = LocalDate.now(),
                semester = selectedSemester.value,
                courageLevel = courageLevel.value
            )
            _verdict.value = result
            _isEvaluating.value = false
        }
    }

    fun incrementAbsence(subject: SubjectEntity, count: Int = 1) {
        viewModelScope.launch {
            repository.incrementAbsence(subject, count)
            // Se já tiver veredito na tela, atualiza automaticamente
            if (_verdict.value != null) {
                consultOracle()
            }
        }
    }

    fun decrementAbsence(subject: SubjectEntity, count: Int = 1) {
        viewModelScope.launch {
            repository.decrementAbsence(subject, count)
            if (_verdict.value != null) {
                consultOracle()
            }
        }
    }

    fun rerollExcuse() {
        viewModelScope.launch {
            val currentVerdict = _verdict.value ?: return@launch
            val newExcuse = repository.getRandomExcuse()
            when (currentVerdict) {
                is SkipVerdict.Allowed -> {
                    _verdict.value = currentVerdict.copy(excuse = newExcuse)
                }
                is SkipVerdict.WarningSafetyMargin -> {
                    _verdict.value = currentVerdict.copy(excuse = newExcuse)
                }
                else -> {}
            }
        }
    }

    fun clearFeedback() {
        _userFeedback.value = null
    }

    fun showFeedback(message: String) {
        _userFeedback.value = message
    }

    class Factory(
        private val repository: AcademicRepository,
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository, preferencesRepository) as T
        }
    }
}

