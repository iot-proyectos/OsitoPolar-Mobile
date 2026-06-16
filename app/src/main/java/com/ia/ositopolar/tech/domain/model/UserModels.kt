package com.ia.ositopolar.tech.domain.model

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

// Archivo: TelemetryModels.kt
data class Device(
    val id: String,
    val idUser: String? = null
)

data class Temperature(
    val id: String,
    val celsius: Float,
    val idDevice: String
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
    val imagen: String, // URL o Base64
    val userId: String,
    val devices: List<Device> = emptyList()
)

data class Mapping(
    val idSection: String,
    val idDevice: String,
    val x: Float,
    val y: Float
)