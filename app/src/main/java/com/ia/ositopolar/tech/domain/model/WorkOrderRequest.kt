package com.ia.ositopolar.tech.domain.model

data class WorkOrderRequest(
    val idDevice: String,
    val notes: String,
    val status: String = "Resolved" // Basado en la máquina de estados de tu PDF
)