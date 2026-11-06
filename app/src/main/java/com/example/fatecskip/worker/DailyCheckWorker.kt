package com.example.fatecskip.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.fatecskip.data.local.AppDatabase
import com.example.fatecskip.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class DailyCheckWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val preferences = UserPreferencesRepository(context)
            val notificationsEnabled = preferences.notificationsEnabledFlow.first()
            if (!notificationsEnabled) {
                return Result.success()
            }

            val database = AppDatabase.getDatabase(context)
            val semester = preferences.selectedSemesterFlow.first()
            val courageLevel = preferences.courageLevelFlow.first()
            val nickname = preferences.studentNicknameFlow.first()

            val tomorrow = LocalDate.now().plusDays(1)
            val tomorrowStr = tomorrow.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val tomorrowDayOfWeek = tomorrow.dayOfWeek.value

            // 1. Checa eventos críticos amanhã
            val criticalEvents = database.eventDao().getCriticalEventsByDate(tomorrowStr)
            if (criticalEvents.isNotEmpty()) {
                val event = criticalEvents.first()
                NotificationHelper.showNotification(
                    context = context,
                    title = "🚨 Alerta de Sobrevivência!",
                    message = "Atenção $nickname! Amanhã tem '${event.title}'. Nem inventa de meter o louco, essa vale nota!",
                    notificationId = 1001
                )
                return Result.success()
            }

            // 2. Checa feriado amanhã
            val holidays = database.eventDao().getHolidaysByDate(tomorrowStr)
            if (holidays.isNotEmpty()) {
                val holiday = holidays.first()
                NotificationHelper.showNotification(
                    context = context,
                    title = "🍺 Glória a Deux!",
                    message = "Amanhã é feriado (${holiday.title}) na Fatec. O despertador tá oficialmente demitido!",
                    notificationId = 1002
                )
                return Result.success()
            }

            // 3. Checa matérias de amanhã com risco de limite
            val allSemesterSubjects = database.subjectDao().getSubjectsBySemesterList(semester)
            val tomorrowSubjects = allSemesterSubjects.filter { it.isClassOnDay(tomorrowDayOfWeek) }
            val dangerSubject = tomorrowSubjects.firstOrNull { subject ->
                val threshold = (subject.maxAbsences * courageLevel).toInt()
                (subject.currentAbsences + subject.classesPerDay) >= threshold
            }

            if (dangerSubject != null) {
                val remaining = (dangerSubject.maxAbsences - dangerSubject.currentAbsences).coerceAtLeast(0)
                NotificationHelper.showNotification(
                    context = context,
                    title = "💀 Ouvindo a música do Fantástico?",
                    message = "Ei $nickname! Você só tem mais $remaining falta(s) em '${dangerSubject.name}'. Se faltar amanhã, o SIGA vai te pegar!",
                    notificationId = 1003
                )
                return Result.success()
            }

            // 4. Teste ou aviso diário de tranquilidade
            NotificationHelper.showNotification(
                context = context,
                title = "🔮 Oráculo da Falta informa:",
                message = "Salve $nickname! Amanhã o dia está favorável. Suas faltas estão dentro da margem de segurança. Durma em paz!",
                notificationId = 1004
            )

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val PERIODIC_WORK_TAG = "fatec_daily_check_work"

        fun schedulePeriodicCheck(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<DailyCheckWorker>(24, TimeUnit.HOURS)
                .addTag(PERIODIC_WORK_TAG)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun scheduleTestNotification(context: Context, delaySeconds: Long = 5) {
            val workRequest = OneTimeWorkRequestBuilder<DailyCheckWorker>()
                .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
                .addTag("fatec_test_work")
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}

