package com.example.fatecskip.data.remote

import com.example.fatecskip.data.remote.dto.FatecDataDto
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface FatecApiService {

    @GET
    suspend fun getFatecData(@Url url: String): FatecDataDto

    companion object {
        private const val DEFAULT_BASE_URL = "https://raw.githubusercontent.com/"

        fun create(): FatecApiService {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(DEFAULT_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FatecApiService::class.java)
        }
    }
}

