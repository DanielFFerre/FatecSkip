package com.example.fatecskip.data.repository

import com.example.fatecskip.data.local.DefaultFatecData
import com.example.fatecskip.data.local.EventDao
import com.example.fatecskip.data.local.ExcuseDao
import com.example.fatecskip.data.local.SubjectDao
import com.example.fatecskip.data.local.entities.EventEntity
import com.example.fatecskip.data.local.entities.ExcuseEntity
import com.example.fatecskip.data.local.entities.SubjectEntity
import com.example.fatecskip.data.preferences.UserPreferencesRepository
import com.example.fatecskip.data.remote.FatecApiService
import com.example.fatecskip.domain.model.SkipVerdict
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class AcademicRepository(
    private val subjectDao: SubjectDao,
    private val eventDao: EventDao,
    private val excuseDao: ExcuseDao,
    private val apiService: FatecApiService,
    private val preferencesRepository: UserPreferencesRepository
) {

    fun getSubjectsForSemester(semester: Int): Flow<List<SubjectEntity>> {
        return subjectDao.getSubjectsBySemester(semester)
    }

    fun getAllEvents(): Flow<List<EventEntity>> {
        return eventDao.getAllEvents()
    }

    fun getAllExcuses(): Flow<List<ExcuseEntity>> {
        return excuseDao.getAllExcuses()
    }

    suspend fun incrementAbsence(subject: SubjectEntity, count: Int = 1) {
        val updated = subject.copy(
            currentAbsences = (subject.currentAbsences + count).coerceAtMost(subject.maxAbsences + 20)
        )
        subjectDao.updateSubject(updated)
    }

    suspend fun decrementAbsence(subject: SubjectEntity, count: Int = 1) {
        val updated = subject.copy(
            currentAbsences = (subject.currentAbsences - count).coerceAtLeast(0)
        )
        subjectDao.updateSubject(updated)
    }

    suspend fun addEvent(title: String, date: String, type: String, isCritical: Boolean): Long {
        val event = EventEntity(
            title = title.trim(),
            date = date.trim(),
            type = type,
            isCritical = isCritical
        )
        return eventDao.insertEvent(event)
    }

    suspend fun deleteEvent(event: EventEntity) {
        eventDao.deleteEvent(event)
    }

    suspend fun deleteEventById(id: Long) {
        eventDao.deleteEventById(id)
    }

    suspend fun getRandomExcuse(): String {
        val dbExcuse = excuseDao.getRandomExcuse()?.text
        if (!dbExcuse.isNullOrBlank()) return dbExcuse
        val defaults = DefaultFatecData.getDefaultExcuses()
        return defaults.random().text
    }

    /**
     * Algoritmo central do "Posso Faltar Hoje?" / Oráculo da Preguiça.
     * Avalia o dia atual contra feriados, eventos críticos e limites de faltas no SIGA.
     */
    suspend fun evaluateSkipVerdict(
        date: LocalDate = LocalDate.now(),
        semester: Int,
        courageLevel: Float
    ): SkipVerdict {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE) // YYYY-MM-DD
        val dayOfWeek = date.dayOfWeek.value // 1 (Segunda) .. 7 (Domingo)

        // 1. Verificação de Domingo ou Feriado Oficial
        val holidaysToday = eventDao.getHolidaysByDate(dateString)
        if (holidaysToday.isNotEmpty()) {
            val holidayTitle = holidaysToday.first().title
            val messages = listOf(
                "Levanta pra quê? Hoje é feriado ($holidayTitle) na Fatec! O campus tá trancado e o cobertor te abraçou.",
                "Glória a Deux! Hoje é $holidayTitle. Desliga o despertador e volta a dormir com a bênção da coordenação!",
                "Nem o diretor pisou na Fatec hoje por causa de $holidayTitle. Fique de boas na sua cama!"
            )
            return SkipVerdict.NoClassOrHoliday(holidayTitle, messages.random())
        }

        if (dayOfWeek == 7) { // Domingo
            return SkipVerdict.NoClassOrHoliday(
                "Domingo",
                "Hoje é domingo, seu emocionado! Quem vai pra faculdade de domingo? Volta a dormir!"
            )
        }

        // Matérias do dia
        val subjectsToday = subjectDao.getSubjectsBySemesterAndDay(semester, dayOfWeek)
        if (subjectsToday.isEmpty()) {
            val messages = listOf(
                "Hoje não tem nenhuma aula cadastrada para o seu semestre (${semester}º). Pode maratonar série em paz!",
                "Grade zerada hoje! O Oráculo informa que o seu único compromisso é descansar a mente.",
                "Sem aulas hoje no SIGA para o ${semester}º semestre. Aproveite o dia sem estresse!"
            )
            return SkipVerdict.NoClassOrHoliday("Sem aulas hoje", messages.random())
        }

        // 2. Verificação de Evento Crítico (Provas P1/P2, entregas de TG, etc.)
        val criticalEventsToday = eventDao.getCriticalEventsByDate(dateString)
        if (criticalEventsToday.isNotEmpty()) {
            val eventTitle = criticalEventsToday.first().title
            val messages = listOf(
                "LEVANTA AGORA! Hoje tem '$eventTitle'! Se faltar, já pode ir pesquisando o horário da DP no semestre que vem!",
                "PROIBIDO FALTAR! Hoje tem evento com peso na alma: '$eventTitle'. Nem inventa de meter o louco!",
                "🚨 ALERTA VERMELHO! '$eventTitle' hoje. Se você faltar, nem o coordenador vai conseguir te defender!"
            )
            return SkipVerdict.ForbiddenCriticalEvent(eventTitle, messages.random())
        }

        // 3. Verificação de Limite de Faltas (Reprovação por Falta no SIGA - Regra 25%)
        for (subject in subjectsToday) {
            val simulatedAbsences = subject.currentAbsences + subject.classesPerDay
            if (simulatedAbsences > subject.maxAbsences) {
                val messages = listOf(
                    "PROIBIDO FALTAR! Se faltar hoje, você REPROVA por falta no SIGA em '${subject.name}' ($simulatedAbsences/${subject.maxAbsences})!",
                    "💀 PERIGO FATAL! Mais uma falta hoje e o SIGA cancela seu semestre em '${subject.name}'! Levanta dessa cama agora!",
                    "NEM PENSE EM FALTAR! O fantasma da DP em '${subject.name}' já está puxando seu pé!"
                )
                return SkipVerdict.ForbiddenAbsenceLimit(
                    subject.name,
                    subject.currentAbsences,
                    subject.maxAbsences,
                    messages.random()
                )
            }
        }

        // 4. Verificação de Margem de Segurança (Nível de Coragem do Aluno)
        val excuse = getRandomExcuse()
        for (subject in subjectsToday) {
            val dangerThreshold = (subject.maxAbsences * courageLevel).toInt()
            val simulatedAbsences = subject.currentAbsences + subject.classesPerDay
            if (simulatedAbsences > dangerThreshold) {
                val messages = listOf(
                    "Por sua conta e risco! Sem provas hoje, mas faltar em '${subject.name}' vai te deixar a 1 passo de dar bom dia pro coordenador (${subject.currentAbsences}/${subject.maxAbsences}).",
                    "CUIDADO, GUERREIRO! A corda tá esticando em '${subject.name}'. Vai mesmo gastar sua última carta na manga hoje?",
                    "ZONA DE RISCO! Se faltar hoje em '${subject.name}', nas próximas semanas até dor de dente vai ter que virar atestado!"
                )
                return SkipVerdict.WarningSafetyMargin(
                    subject.name,
                    subject.currentAbsences,
                    subject.maxAbsences,
                    messages.random(),
                    excuse
                )
            }
        }

        // 5. Veredito Verde (Liberado!)
        val allowedMessages = listOf(
            "Abençoado pelo Oráculo! Sem provas críticas e faltas sob total controle. A cama te chama, guerreiro!",
            "LIBERADO COM LOUVOR! Faltar hoje é 100% taticamente seguro. O SIGA nem vai notar sua ausência!",
            "Pode meter o louco hoje! Seu saldo de faltas tá limpo e não tem nada crítico no radar. Bom descanso!",
            "Oráculo aprovou o sono! Hoje a aula pode esperar e o cobertor venceu por nocaute técnico."
        )
        return SkipVerdict.Allowed(
            customMessage = allowedMessages.random(),
            excuse = excuse
        )
    }

    /**
     * Sincroniza a grade da Fatec, feriados e desculpas a partir do endpoint JSON.
     * Caso falhe por conexão ou URL inválida, garante fallback usando os dados padrão.
     */
    suspend fun syncFromApi(targetSemester: Int, customUrl: String? = null): Result<String> {
        return try {
            val endpoint = customUrl?.ifBlank { null } ?: UserPreferencesRepository.DEFAULT_API_URL
            val apiData = try {
                apiService.getFatecData(endpoint)
            } catch (e: Exception) {
                null
            }

            if (apiData != null) {
                // Atualiza / insere feriados
                val holidays = apiData.semesterInfo?.holidays?.map {
                    EventEntity(
                        title = it.title,
                        date = it.date,
                        type = "HOLIDAY",
                        isCritical = false
                    )
                }
                if (!holidays.isNullOrEmpty()) {
                    eventDao.clearHolidays()
                    eventDao.insertEvents(holidays)
                }

                // Atualiza / insere desculpas
                val excuses = apiData.excuses?.map { ExcuseEntity(text = it) }
                if (!excuses.isNullOrEmpty()) {
                    excuseDao.insertExcuses(excuses)
                }

                // Atualiza matérias do semestre alvo ou de todos os semestres
                apiData.curriculums?.forEach { (semesterKey, subjectDtos) ->
                    val semInt = semesterKey.toIntOrNull() ?: 1
                    val existing = subjectDao.getSubjectsBySemesterList(semInt)
                    val existingMap = existing.associateBy { it.code }

                    val subjectEntities = subjectDtos.map { dto ->
                        val prev = existingMap[dto.code]
                        SubjectEntity(
                            id = prev?.id ?: 0,
                            code = dto.code,
                            name = dto.name,
                            semester = semInt,
                            dayOfWeek = dto.dayOfWeek,
                            classesPerDay = dto.classesPerDay,
                            currentAbsences = prev?.currentAbsences ?: 0,
                            maxAbsences = dto.maxAbsences
                        )
                    }
                    subjectDao.insertSubjects(subjectEntities)
                }

                Result.success("Sincronização com a API concluída com sucesso! Grade e eventos atualizados.")
            } else {
                // Fallback offline seguro com a grade oficial exata do PDF
                subjectDao.clearAllSubjects()
                subjectDao.insertSubjects(DefaultFatecData.getDefaultSubjects())
                eventDao.clearHolidays()
                eventDao.insertEvents(DefaultFatecData.getDefaultHolidays())
                excuseDao.insertExcuses(DefaultFatecData.getDefaultExcuses())
                Result.success("Modo offline: Grade oficial de DSM de 2026 recarregada com sucesso!")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

