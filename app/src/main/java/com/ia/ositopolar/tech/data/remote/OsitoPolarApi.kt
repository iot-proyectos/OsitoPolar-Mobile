package com.ia.ositopolar.tech.data.remote

import com.ia.ositopolar.tech.domain.model.Device
import com.ia.ositopolar.tech.domain.model.Humidity
import com.ia.ositopolar.tech.domain.model.Mapping
import com.ia.ositopolar.tech.domain.model.Section
import com.ia.ositopolar.tech.domain.model.Subscription
import com.ia.ositopolar.tech.domain.model.Temperature
import com.ia.ositopolar.tech.domain.model.User
import com.ia.ositopolar.tech.domain.model.UserMetrics
import com.ia.ositopolar.tech.domain.model.AuthResponseData
import com.ia.ositopolar.tech.domain.model.CreateDeviceRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OsitoPolarApi {

    // --- AUTH ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponseData>> // <--- Cambiado a AuthResponseData

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponseData>> // <--- Cambiado a AuthResponseData

    @POST("auth/logout")
    suspend fun logout(@Body request: Map<String, String>): Response<ApiResponse<Any>>

    // --- USERS & METRICS ---
    @GET("users/me")
    suspend fun getUserProfile(): Response<ApiResponse<User>>

    @GET("subscriptions/me")
    suspend fun getSubscription(): Response<ApiResponse<Subscription>>

    @GET("metrics")
    suspend fun getUserMetrics(): Response<ApiResponse<List<UserMetrics>>>


    // --- DEVICES & SECTIONS ---
    @GET("devices")
    suspend fun getAllDevices(): Response<ApiResponse<List<Device>>>

    @GET("sections")
    suspend fun getSections(): Response<ApiResponse<List<Section>>>

    @GET("sections/{sectionId}/mappings")
    suspend fun getMappings(@Path("sectionId") sectionId: String): Response<ApiResponse<List<Mapping>>>


    // --- TELEMETRY IOT ---
    @GET("temperature/{deviceId}")
    suspend fun getCurrentTemperature(@Path("deviceId") deviceId: String): Response<ApiResponse<Temperature>>

    @GET("temperature/{deviceId}/history")
    suspend fun getTemperatureHistory(@Path("deviceId") deviceId: String): Response<ApiResponse<List<Temperature>>>

    @GET("humidity/{deviceId}")
    suspend fun getCurrentHumidity(@Path("deviceId") deviceId: String): Response<ApiResponse<Humidity>>

    @GET("energy/{deviceId}")
    suspend fun getEnergyConsumption(@Path("deviceId") deviceId: String): Response<ApiResponse<Any>>


    // --- ALERTS ---
    @GET("alerts/unread-count")
    suspend fun getUnreadAlertsCount(): Response<ApiResponse<Map<String, Int>>>

    // --- WORK ORDERS (MANTENIMIENTO) ---
    @POST("workorder")
    suspend fun createWorkOrder(@Body request: com.ia.ositopolar.tech.domain.model.WorkOrderRequest): Response<ApiResponse<Any>>

    @POST("devices") // O la ruta que use tu backend para crear equipos
    suspend fun createDevice(
        @Body request: CreateDeviceRequest
    ): Response<ApiResponse<Any>> // Ajusta el 'Any' si el backend te devuelve el equipo creado
}

// Ver anterior
//package com.ia.ositopolar.tech.data.remote
//
//import com.ia.ositopolar.tech.domain.model.Device
//import com.ia.ositopolar.tech.domain.model.Humidity
//import com.ia.ositopolar.tech.domain.model.Mapping
//import com.ia.ositopolar.tech.domain.model.Section
//import com.ia.ositopolar.tech.domain.model.Subscription
//import com.ia.ositopolar.tech.domain.model.Temperature
//import com.ia.ositopolar.tech.domain.model.User
//import com.ia.ositopolar.tech.domain.model.UserMetrics
//import com.ia.ositopolar.tech.domain.model.WorkOrderRequest
//import retrofit2.Response
//import retrofit2.http.Body
//import retrofit2.http.GET
//import retrofit2.http.POST
//import retrofit2.http.Path
//
//interface OsitoPolarApi {
//
//    // --- AUTENTICACIÓN Y USUARIO ---
//
//    @POST("/api/v1/login")
//    suspend fun login(@Body request: Map<String, String>): Response<User>
//
//    @POST("/api/v1/register")
//    suspend fun register(@Body user: User): Response<User>
//
//    @GET("/api/v1/user/{id}")
//    suspend fun getUser(@Path("id") userId: String): Response<User>
//
//    @GET("/api/v1/subscription/{userId}")
//    suspend fun getSubscription(@Path("userId") userId: String): Response<Subscription>
//
//    @GET("/api/v1/userMetrics/{userId}")
//    suspend fun getUserMetrics(@Path("userId") userId: String): Response<List<UserMetrics>>
//
//
//    // --- MAPEO Y EQUIPOS ---
//
//    @GET("/api/v1/device")
//    suspend fun getAllDevices(): Response<List<Device>>
//
//    // Endpoints inferidos por la lógica de Mapeo
//    @GET("/api/v1/sections/{userId}")
//    suspend fun getSections(@Path("userId") userId: String): Response<List<Section>>
//
//    @GET("/api/v1/mapping/{idSection}")
//    suspend fun getMappings(@Path("idSection") idSection: String): Response<List<Mapping>>
//
//
//    // --- TELEMETRÍA IOT (ESP32 / DHT22) ---
//
//    @GET("/api/v1/temperature/{idDevice}/{historial}")
//    suspend fun getTemperatureHistory(
//        @Path("idDevice") idDevice: String,
//        @Path("historial") historial: String
//    ): Response<List<Temperature>>
//
//    @GET("/api/v1/temperature/{idDevice}") // Asumiendo que quieres la última lectura
//    suspend fun getCurrentTemperature(@Path("idDevice") idDevice: String): Response<Temperature>
//
//    @GET("/api/v1/humidity/{idDevice}")
//    suspend fun getCurrentHumidity(@Path("idDevice") idDevice: String): Response<Humidity>
//
//    @GET("/api/v1/energyConsumption/{idDevice}")
//    suspend fun getEnergyConsumption(@Path("idDevice") idDevice: String): Response<Any> // Ajustar cuando tengas el modelo de Energía
//
//    // --- ÓRDENES DE TRABAJO (MANTENIMIENTO) ---
//
//    @POST("/api/v1/workorder")
//    suspend fun createWorkOrder(@Body request: WorkOrderRequest): Response<Any>
//}