package com.ia.ositopolar.tech.domain.model

import com.google.gson.annotations.SerializedName

// Archivo: UserModels.kt
enum class TypeSubs {
    NONE, BOUGHT, RENTING, BUSINESS
}

data class Subscription(
    val id: String,
    val subscription: TypeSubs,
    val idUser: String,
    val exp: String,
    val createdAt: String
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val idSubcription: Subscription? = null
)

// --- NUEVO DTO PARA AUTENTICACIÓN ---
// Esto le dice a Kotlin: "Cuando inicie sesión, recibiré un token y opcionalmente los datos del usuario"
data class AuthResponseData(
    val token: String? = null,
    val accessToken: String? = null,
    val user: User? = null
)

// Archivo: TelemetryModels.kt
data class Device(
    val id: String,
    val idUser: String? = null
)

data class Temperature(
    val id: String,
    val celsius: Float,
    @SerializedName("deviceId") val idDevice: String
)

data class Humidity(
    val id: String,
    val percentage: Float,
    val idDevice: String
)

data class UserMetrics(
    val id: String,
    val idUser: String,
    val idDevice: String,
    val inferior: Float,
    val superior: Float
)

// Archivo: MappingModels.kt
data class Section(
    val id: String,
    @SerializedName("imageUrl") val imagen: String,
    val userId: String,
    val devices: List<Device> = emptyList()
)

data class Mapping(
    val id: String,
    val x: Float,
    val y: Float,
    val device: Device // <--- Ahora recibimos el objeto completo, tal como viene en el JSON
) {
    // Agregamos este pequeño truco (propiedad calculada) para que no tengas
    // que modificar tu DashboardScreen ni tu ViewModel.
    // Cuando el código pida 'idDevice', simplemente sacará el ID de adentro del objeto.
    val idDevice: String
        get() = device.id
}