package com.example.fatecskip.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa uma matéria da grade da Fatec com controle de faltas.
 * dayOfWeek: 1 = Segunda-feira ... 6 = Sábado, 7 = Domingo
 * daysOfWeek: suporte a múltiplos dias da semana (ex: "2,4" para Terça e Quinta)
 */
@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
    val semester: Int,
    val dayOfWeek: Int = 1,
    val daysOfWeek: String = "",
    val classesPerDay: Int = 4,
    val currentAbsences: Int = 0,
    val maxAbsences: Int = 20,
    val isCustomized: Boolean = false,
    val isExtension: Boolean = false,
    val professor: String = "",
    val classTime: String = "19:00 - 22:30"
) {
    /**
     * Verifica se a matéria tem aulas no dia da semana informado (1 = Seg .. 6 = Sáb)
     */
    fun isClassOnDay(targetDay: Int): Boolean {
        if (daysOfWeek.isNotBlank()) {
            return daysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() }.contains(targetDay)
        }
        return dayOfWeek == targetDay
    }

    /**
     * Retorna os dias da semana formatados para exibição (ex: "Terça e Quinta")
     */
    fun getFormattedDays(): String {
        val daysList = if (daysOfWeek.isNotBlank()) {
            daysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() }
        } else {
            listOf(dayOfWeek)
        }
        return daysList.joinToString(" e ") { d ->
            when (d) {
                1 -> "Segunda"
                2 -> "Terça"
                3 -> "Quarta"
                4 -> "Quinta"
                5 -> "Sexta"
                6 -> "Sábado"
                else -> "Dia livre"
            }
        }
    }
}
