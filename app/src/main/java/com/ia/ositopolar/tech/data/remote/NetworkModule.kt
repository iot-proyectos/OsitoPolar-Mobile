package com.ia.ositopolar.tech.data.remote

import com.ia.ositopolar.tech.data.repository.RemoteOsitoPolarRepository
import com.ia.ositopolar.tech.domain.repository.OsitoPolarRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // <--- NUEVO IMPORT
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:3000/api/v1/"

    // 1. Creamos nuestro interceptor de "Rayos X"
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        println("OSITOPOLAR_API: $message")
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY // Nivel BODY nos muestra el JSON completo
    }

    // 2. Lo conectamos a nuestro cliente HTTP
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // <--- Agregamos el espía aquí
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            SessionManager.token?.let { jwt ->
                requestBuilder.addHeader("Authorization", "Bearer $jwt")
            }
            chain.proceed(requestBuilder.build())
        }.build()

    val api: OsitoPolarApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OsitoPolarApi::class.java)
    }

    val repository: OsitoPolarRepository by lazy {
        RemoteOsitoPolarRepository(api)
    }
}