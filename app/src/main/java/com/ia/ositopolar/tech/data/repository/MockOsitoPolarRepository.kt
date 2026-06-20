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
        // Simulamos una respuesta exitosa con los nuevos parámetros
        val mockMappings = listOf(
            Mapping(
                id = "mock-map-1",
                x = 150f,
                y = 250f,
                device = Device(id = "DEV-001") // Creamos un dispositivo de prueba dentro
            ),
            Mapping(
                id = "mock-map-2",
                x = 300f,
                y = 400f,
                device = Device(id = "DEV-002")
            )
        )
        return Result.success(mockMappings)
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
    override suspend fun createDevice(name: String, serialNumber: String, x: Float, y: Float): Result<Boolean> {
        // 1. Simulamos que viaja por internet (medio segundo)
        kotlinx.coroutines.delay(500)

        // 2. Imprimimos en consola para saber que el Mock está funcionando
        println("OSITOPOLAR_DEBUG: [MOCK] Fingiendo crear el equipo '$name' (Serie: $serialNumber) en X:$x, Y:$y")

        // 3. Devolvemos un éxito rotundo
        return Result.success(true)
    }
}

// Instancia real usando Retrofit (LUEGO DE QUE TENGAMOS EL BACK)
// val api = Retrofit.Builder().baseUrl("https://tu-api.com").build().create(OsitoPolarApi::class.java)
// val repositorioReal = RemoteOsitoPolarRepository(api)