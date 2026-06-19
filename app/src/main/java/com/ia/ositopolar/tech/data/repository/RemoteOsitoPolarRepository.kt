package com.ia.ositopolar.tech.data.repository

import com.ia.ositopolar.tech.data.remote.OsitoPolarApi
import com.ia.ositopolar.tech.domain.model.Humidity
import com.ia.ositopolar.tech.domain.model.Mapping
import com.ia.ositopolar.tech.domain.model.Section
import com.ia.ositopolar.tech.domain.model.Temperature
import com.ia.ositopolar.tech.domain.model.WorkOrderRequest
import com.ia.ositopolar.tech.domain.repository.OsitoPolarRepository

class RemoteOsitoPolarRepository(
    private val api: OsitoPolarApi
) : OsitoPolarRepository {

    override suspend fun getSections(userId: String): Result<List<Section>> {
        return try {
            // El backend usa el Token para identificar al usuario, no la URL
            val response = api.getSections()

            // Verificamos que la llamada HTTP fue exitosa, que el JSON dice "success: true" y que hay "data"
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!) // Extraemos la lista
            } else {
                Result.failure(Exception("Error en la API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDeviceMappings(sectionId: String): Result<List<Mapping>> {
        return try {
            val response = api.getMappings(sectionId)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Error en la API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDeviceTemperature(deviceId: String): Result<Temperature> {
        return try {
            val response = api.getCurrentTemperature(deviceId)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Error en la API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDeviceHumidity(deviceId: String): Result<Humidity> {
        return try {
            val response = api.getCurrentHumidity(deviceId)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Error en la API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveWorkOrder(request: WorkOrderRequest): Result<Boolean> {
        return try {
            val response = api.createWorkOrder(request)
            // Aquí solo nos importa que haya llegado con éxito
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al guardar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}