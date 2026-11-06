package com.example.fatecskip.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa um evento no calendário (Prova P1/P2, Trabalho Crítico, Feriado ou Aviso).
 * isCritical = true força a proibição de faltas (Veredito Vermelho).
 */
@Entity(tableName = "academic_events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: String, // Formato YYYY-MM-DD
    val type: String, // EXAM, ASSIGNMENT, HOLIDAY, OTHER
    val isCritical: Boolean = false
)

