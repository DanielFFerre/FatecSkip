package com.example.fatecskip.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa uma matéria da grade da Fatec com controle de faltas.
 * dayOfWeek: 1 = Segunda-feira ... 6 = Sábado, 7 = Domingo
 */
@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
    val semester: Int,
    val dayOfWeek: Int,
    val classesPerDay: Int = 4,
    val currentAbsences: Int = 0,
    val maxAbsences: Int = 20
)

