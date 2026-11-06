package com.example.fatecskip.ui.screens.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fatecskip.data.local.entities.EventEntity
import com.example.fatecskip.data.repository.AcademicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventsViewModel(
    private val repository: AcademicRepository
) : ViewModel() {

    private val _filter = MutableStateFlow("ALL") // ALL, CRITICAL, HOLIDAY
    val filter: StateFlow<String> = _filter.asStateFlow()

    private val _isAddDialogOpen = MutableStateFlow(false)
    val isAddDialogOpen: StateFlow<Boolean> = _isAddDialogOpen.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    val filteredEvents: StateFlow<List<EventEntity>> = combine(
        repository.getAllEvents(),
        _filter
    ) { events, currentFilter ->
        when (currentFilter) {
            "CRITICAL" -> events.filter { it.isCritical }
            "HOLIDAY" -> events.filter { it.type == "HOLIDAY" }
            else -> events
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: String) {
        _filter.value = filter
    }

    fun openAddDialog(open: Boolean) {
        _isAddDialogOpen.value = open
    }

    fun addEvent(title: String, date: String, type: String, isCritical: Boolean) {
        if (title.isBlank() || date.isBlank()) {
            _feedbackMessage.value = "Preencha o título e a data do evento!"
            return
        }

        viewModelScope.launch {
            repository.addEvent(
                title = title.trim(),
                date = date.trim(),
                type = type,
                isCritical = isCritical
            )
            _isAddDialogOpen.value = false
            _feedbackMessage.value = "Evento adicionado na Agenda do Caos!"
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
            _feedbackMessage.value = "Evento removido com sucesso!"
        }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    class Factory(
        private val repository: AcademicRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EventsViewModel(repository) as T
        }
    }
}

