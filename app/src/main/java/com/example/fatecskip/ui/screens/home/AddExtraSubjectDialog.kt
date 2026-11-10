package com.example.fatecskip.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AddExtraSubjectDialog(
    viewModel: HomeViewModel,
    onDismiss: () -> Unit
) {
    var searchSemester by remember { mutableIntStateOf(1) }

    // Obter matérias disponíveis via HomeViewModel
    val availableSubjects = remember(searchSemester) {
        viewModel.getAvailableSubjectsForSemester(searchSemester)
    }

    var selectedSubjectCode by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Incluir Matéria 📚",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Escolha o semestre original da disciplina que deseja adicionar:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (1..3).forEach { sem ->
                        FilterChip(
                            selected = searchSemester == sem,
                            onClick = { searchSemester = sem; selectedSubjectCode = null },
                            label = { Text("${sem}º") }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (4..6).forEach { sem ->
                        FilterChip(
                            selected = searchSemester == sem,
                            onClick = { searchSemester = sem; selectedSubjectCode = null },
                            label = { Text("${sem}º") }
                        )
                    }
                }

                Text(
                    text = "Disciplinas disponíveis:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )

                // Lista simulada em dropdown ou chip. Por simplicidade visual, Column com scroll seria ideal
                // Vamos usar FilterChips empilhados ou OutlinedButtons simples (pois o AlertDialog em M3 faz scroll automático)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableSubjects.forEach { sub ->
                        FilterChip(
                            selected = selectedSubjectCode == sub.code,
                            onClick = { selectedSubjectCode = sub.code },
                            label = { Text(sub.name, maxLines = 1) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sub = availableSubjects.find { it.code == selectedSubjectCode }
                    if (sub != null) {
                        viewModel.addSubjectFromOtherSemester(sub)
                        onDismiss()
                    }
                },
                enabled = selectedSubjectCode != null,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancelar")
            }
        }
    )
}
