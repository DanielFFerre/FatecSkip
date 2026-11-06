package com.example.fatecskip.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade para armazenar desculpas esfarrapadas e engraçadas para o aluno mandar no WhatsApp da turma.
 */
@Entity(tableName = "excuses")
data class ExcuseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String
)

