package com.ia.ositopolar.tech.data.repository

import com.ia.ositopolar.tech.domain.model.Device
import com.ia.ositopolar.tech.domain.model.Mapping
import com.ia.ositopolar.tech.domain.model.Section
import com.ia.ositopolar.tech.domain.model.Temperature
import com.ia.ositopolar.tech.domain.repository.OsitoPolarRepository
import kotlinx.coroutines.delay
import com.ia.ositopolar.tech.domain.model.Humidity // <--- IMPORT FALTANTE
import com.ia.ositopolar.tech.domain.model.WorkOrderRequest

class MockOsitoPolarRepository : OsitoPolarRepository {

    override suspend fun getSections(userId: String): Result<List<Section>> {
        delay(1000)
        val mockDevices = listOf(Device(id = "DEV-001"), Device(id = "DEV-002"))
        val section = Section(
            id = "SEC-A1",
            imagen = "https://ejemplo.com/plano.jpg",
            userId = userId,
            devices = mockDevices
        )
        return Result.success(listOf(section))
    }

    override suspend fun getDeviceMappings(sectionId: String): Result<List<Mapping>> {
        delay(500)
        val mappings = listOf(
            Mapping(idSection = sectionId, idDevice = "DEV-001", x = 120.5f, y = 340.0f),
            Mapping(idSection = sectionId, idDevice = "DEV-002", x = 500.0f, y = 450.5f)
        )
        return Result.success(mappings)
    }

    override suspend fun getDeviceTemperature(deviceId: String): Result<Temperature> {
        delay(300)
        return Result.success(Temperature(id = "TEMP-1", celsius = 24.5f, idDevice = deviceId))
    }
    override suspend fun getDeviceHumidity(deviceId: String): Result<Humidity> {
        delay(300)
        // Simulamos una lectura del DHT22: Humedad alta (ej. puerta abierta mucho tiempo)
        return Result.success(Humidity(id = "HUM-1", percentage = 85.5f, idDevice = deviceId))
    }
    override suspend fun saveWorkOrder(request: WorkOrderRequest): Result<Boolean> {
        delay(1000) // Simulamos que viaja por internet
        return Result.success(true) // Siempre responde "Éxito" en el mock
    }
}

// Instancia real usando Retrofit (LUEGO DE QUE TENGAMOS EL BACK)
// val api = Retrofit.Builder().baseUrl("https://tu-api.com").build().create(OsitoPolarApi::class.java)
// val repositorioReal = RemoteOsitoPolarRepository(api)