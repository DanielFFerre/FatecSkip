package com.example.fatecskip

import android.app.Application
import com.example.fatecskip.data.local.AppDatabase
import com.example.fatecskip.data.preferences.UserPreferencesRepository
import com.example.fatecskip.data.remote.FatecApiService
import com.example.fatecskip.data.repository.AcademicRepository
import com.example.fatecskip.worker.DailyCheckWorker
import com.example.fatecskip.worker.NotificationHelper

class FatecSkipApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    lateinit var apiService: FatecApiService
        private set

    lateinit var repository: AcademicRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getDatabase(this)
        preferencesRepository = UserPreferencesRepository(this)
        apiService = FatecApiService.create()

        repository = AcademicRepository(
            subjectDao = database.subjectDao(),
            eventDao = database.eventDao(),
            excuseDao = database.excuseDao(),
            apiService = apiService,
            preferencesRepository = preferencesRepository
        )

        NotificationHelper.createNotificationChannel(this)
        DailyCheckWorker.schedulePeriodicCheck(this)
    }

    companion object {
        lateinit var instance: FatecSkipApp
            private set
    }
}

