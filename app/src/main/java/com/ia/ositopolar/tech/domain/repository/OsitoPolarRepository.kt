package com.ia.ositopolar.tech.domain.repository

import com.ia.ositopolar.tech.domain.model.Mapping
import com.ia.ositopolar.tech.domain.model.Section
import com.ia.ositopolar.tech.domain.model.Temperature
import com.ia.ositopolar.tech.domain.model.Humidity // <--- IMPORT FALTANTE
import com.ia.ositopolar.tech.domain.model.WorkOrderRequest

interface OsitoPolarRepository {
    suspend fun getSections(userId: String): Result<List<Section>>
    suspend fun getDeviceMappings(sectionId: String): Result<List<Mapping>>
    suspend fun getDeviceTemperature(deviceId: String): Result<Temperature>
    suspend fun getDeviceHumidity(deviceId: String): Result<Humidity>
    suspend fun saveWorkOrder(request: WorkOrderRequest): Result<Boolean>
    suspend fun createDevice(name: String, serialNumber: String, x: Float, y: Float): Result<Boolean>
}

