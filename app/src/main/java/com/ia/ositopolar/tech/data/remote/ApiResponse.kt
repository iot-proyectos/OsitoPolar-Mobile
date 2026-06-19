package com.ia.ositopolar.tech.data.remote

// Envoltura principal para todas las peticiones
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: ApiError?,
    val meta: ApiMeta?
)

data class ApiError(
    val code: String,
    val message: String
)

data class ApiMeta(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)