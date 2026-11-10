package com.example.fatecskip.ui.screens.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fatecskip.data.local.entities.SubjectEntity
import com.example.fatecskip.domain.model.SkipVerdict
import com.example.fatecskip.ui.theme.HolidayBlue
import com.example.fatecskip.ui.theme.PanicRed
import com.example.fatecskip.ui.theme.SafeGreen
import com.example.fatecskip.ui.theme.WarningYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val nickname by viewModel.studentNickname.collectAsState()
    val semester by viewModel.selectedSemester.collectAsState()
    val courageLevel by viewModel.courageLevel.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val verdict by viewModel.verdict.collectAsState()
    val isEvaluating by viewModel.isEvaluating.collectAsState()
    val feedbackMessage by viewModel.userFeedback.collectAsState()

    var showAddDpDialog by remember { mutableStateOf(false) }
    var selectedSubjectForEdit by remember { mutableStateOf<SubjectEntity?>(null) }
    var showAddExtraSubjectDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabeçalho de Boas-vindas
            item {
                HeaderCard(
                    nickname = nickname,
                    semester = semester,
                    courageLevel = courageLevel
                )
            }

            // Botão Principal do Oráculo
            item {
                OracleButtonCard(
                    isEvaluating = isEvaluating,
                    onConsultClick = { viewModel.consultOracle() }
                )
            }

            // Veredito Dinâmico (Animado)
            item {
                AnimatedVisibility(
                    visible = verdict != null,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400))
                ) {
                    verdict?.let { currentVerdict ->
                        VerdictCard(
                            verdict = currentVerdict,
                            onCopyExcuse = { excuse ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Desculpa Fatec", excuse)
                                clipboard.setPrimaryClip(clip)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Desculpa copiada! Cole no zap da sala 🏃💨")
                                }
                            },
                            onRerollExcuse = {
                                viewModel.rerollExcuse()
                            }
                        )
                    }
                }
            }

            // Título da Seção de Matérias + Botão de Adicionar DP
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Minhas Disciplinas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${subjects.size} matérias cadastradas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = { showAddDpDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PostAdd,
                            contentDescription = "Adicionar Matéria",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "+ Matéria Extra", fontSize = 12.sp)
                    }
                }
            }

            // Lista de Matérias
            if (subjects.isEmpty()) {
                item {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📭",
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nenhuma matéria encontrada no ${semester}º semestre.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Vá em 'Sobrevivência' e sincronize a grade da API!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(subjects, key = { it.id }) { subject ->
                    SubjectItemCard(
                        subject = subject,
                        onIncrement = { viewModel.incrementAbsence(subject, subject.classesPerDay) },
                        onDecrement = { viewModel.decrementAbsence(subject, 1) },
                        onEditClick = { selectedSubjectForEdit = subject }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showAddExtraSubjectDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar Matéria Extra",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+ Incluir Matéria de Outro Semestre (Adiantar / Fora de Fase)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showAddDpDialog) {
        AddDpSubjectDialog(
            currentSemester = semester,
            onDismiss = { showAddDpDialog = false },
            onConfirm = { code, name, sem, day, classesPerDay, maxAbsences ->
                viewModel.addCustomSubject(code, name, sem, day, classesPerDay, maxAbsences)
                showAddDpDialog = false
            }
        )
    }

    selectedSubjectForEdit?.let { subject ->
        EditSubjectDialog(
            subject = subject,
            onDismiss = { selectedSubjectForEdit = null },
            onSave = { id, prof, time, days, absences ->
                viewModel.saveSubjectEdits(id, prof, time, days, absences)
                selectedSubjectForEdit = null
            }
        )
    }

    if (showAddExtraSubjectDialog) {
        AddExtraSubjectDialog(
            viewModel = viewModel,
            onDismiss = { showAddExtraSubjectDialog = false }
        )
    }

    selectedSubjectForEdit?.let { subject ->
        EditSubjectDialog(
            subject = subject,
            onDismiss = { selectedSubjectForEdit = null },
            onSave = { id, prof, time, days, absences ->
                viewModel.saveSubjectEdits(id, prof, time, days, absences)
                selectedSubjectForEdit = null
            }
        )
    }

    if (showAddExtraSubjectDialog) {
        AddExtraSubjectDialog(
            viewModel = viewModel,
            onDismiss = { showAddExtraSubjectDialog = false }
        )
    }
}

@Composable
private fun HeaderCard(
    nickname: String,
    semester: Int,
    courageLevel: Float
) {
    val modeName = when {
        courageLevel <= 0.6f -> "Modo Nerdola 🤓"
        courageLevel <= 0.8f -> "Estudante Médio 🎓"
        else -> "Modo Vida Loka 💀"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Salve, $nickname!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "🛌", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "SIGA na Cama • Fatec Diadema",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${semester}º Semestre Base",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = modeName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun OracleButtonCard(
    isEvaluating: Boolean,
    onConsultClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔮 O ORÁCULO DA FALTA",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cruze suas faltas com o Regulamento CEETEPS e saiba se sua cama foi autorizada hoje.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onConsultClick,
                enabled = !isEvaluating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isEvaluating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Consultando o SIGA...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "POSSO FALTAR HOJE?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun VerdictCard(
    verdict: SkipVerdict,
    onCopyExcuse: (String) -> Unit,
    onRerollExcuse: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = verdict.containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = verdict.emoji,
                    fontSize = 36.sp
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = verdict.badgeText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = verdict.contentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = verdict.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = verdict.contentColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = verdict.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = verdict.contentColor.copy(alpha = 0.95f),
                lineHeight = 22.sp
            )

            verdict.suggestedExcuse?.let { excuse ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.18f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "💬 Desculpa oficial pro zap da sala:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = verdict.contentColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "\"$excuse\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal,
                            color = verdict.contentColor
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { onCopyExcuse(excuse) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copiar",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Copiar", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = onRerollExcuse,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Trocar",
                                    modifier = Modifier.size(16.dp),
                                    tint = verdict.contentColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Outra",
                                    fontSize = 12.sp,
                                    color = verdict.contentColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectItemCard(
    subject: SubjectEntity,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onEditClick: () -> Unit
) {
    val progress = (subject.currentAbsences.toFloat() / subject.maxAbsences.toFloat()).coerceIn(0f, 1f)

    val progressColor by animateColorAsState(
        targetValue = when {
            progress < 0.5f -> SafeGreen
            progress < 0.8f -> WarningYellow
            else -> PanicRed
        },
        label = "progressColor"
    )

    // Formatação Monospace de HP de Faltas (Ex: 04/20)
    val formattedHp = String.format("%02d/%02d", subject.currentAbsences, subject.maxAbsences)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (subject.professor.isNotBlank()) {
                        Text(
                            text = "Prof. ${subject.professor} • ${subject.classTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Sem docente • ${subject.classTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${subject.code} • ${subject.getFormattedDays()} • ${subject.classesPerDay} aulas/dia",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (subject.isExtension) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = WarningYellow.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "EXTENSÃO",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarningYellow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Ícone de Lápis + Faltas Badge Monospace Gamer
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = progressColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "$formattedHp faltas",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = progressColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de Progresso Gamer (HP de faltas)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botões de Ação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (progress >= 0.8f) "💀 Sem Exame Final se estourar" else if (progress >= 0.5f) "⚠️ Zona de atenção" else "✅ Faltas sob controle",
                    style = MaterialTheme.typography.bodySmall,
                    color = progressColor,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onDecrement,
                        enabled = subject.currentAbsences > 0,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Diminuir faltas",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Button(
                        onClick = onIncrement,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar faltas",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Faltei (+${subject.classesPerDay})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddDpSubjectDialog(
    currentSemester: Int,
    onDismiss: () -> Unit,
    onConfirm: (code: String, name: String, semester: Int, dayOfWeek: Int, classesPerDay: Int, maxAbsences: Int) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var targetSemester by remember { mutableIntStateOf(currentSemester) }
    var dayOfWeek by remember { mutableIntStateOf(1) }
    var is80Hours by remember { mutableStateOf(true) } // true = 80h (4 aulas/dia), false = 40h (2 aulas/dia)

    val days = listOf(
        1 to "Segunda",
        2 to "Terça",
        3 to "Quarta",
        4 to "Quinta",
        5 to "Sexta",
        6 to "Sábado"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Incluir Matéria Extra 🎒",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Puxe uma matéria de outro semestre do catálogo (Adiantamento / Fora de Fase) ou cadastre manualmente.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Código (Ex: IBD002)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Matéria (Ex: Banco de Dados Rel.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Semestre em que está matriculado:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..3).forEach { sem ->
                        FilterChip(
                            selected = targetSemester == sem,
                            onClick = { targetSemester = sem },
                            label = { Text("${sem}º") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (4..6).forEach { sem ->
                        FilterChip(
                            selected = targetSemester == sem,
                            onClick = { targetSemester = sem },
                            label = { Text("${sem}º") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Text(
                    text = "Dia da Semana da Aula:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                
                val daysChunked = days.chunked(3)
                daysChunked.forEach { rowDays ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowDays.forEach { (dInt, dName) ->
                            FilterChip(
                                selected = dayOfWeek == dInt,
                                onClick = { dayOfWeek = dInt },
                                label = { Text(dName.take(3)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Text(
                    text = "Carga Horária / Aulas por Dia:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = is80Hours,
                        onClick = { is80Hours = true },
                        label = { Text("80h (4 aulas/dia • Máx 20 faltas)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    FilterChip(
                        selected = !is80Hours,
                        onClick = { is80Hours = false },
                        label = { Text("40h (2 aulas/dia • Máx 10 faltas)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val classes = if (is80Hours) 4 else 2
                        val maxAbs = if (is80Hours) 20 else 10
                        val cd = if (code.isBlank()) "DP${(10..99).random()}" else code
                        onConfirm(cd, name, targetSemester, dayOfWeek, classes, maxAbs)
                    }
                },
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
