package com.ia.ositopolar.tech.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ia.ositopolar.tech.data.remote.NetworkModule
import com.ia.ositopolar.tech.domain.model.Mapping
import com.ia.ositopolar.tech.domain.model.Section
import com.ia.ositopolar.tech.domain.model.Temperature
import com.ia.ositopolar.tech.domain.model.Humidity // <--- IMPORTANTE: Importar el modelo de humedad
import com.ia.ositopolar.tech.domain.repository.OsitoPolarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = true,
    val section: Section? = null,
    val mappings: List<Mapping> = emptyList(),
    val errorMessage: String? = null,
    val selectedTemperature: Temperature? = null,
    val selectedHumidity: Humidity? = null, // <--- 1. NUEVA VARIABLE PARA LA HUMEDAD
    val selectedDeviceId: String? = null
)

class DashboardViewModel(
    private val repository: OsitoPolarRepository = NetworkModule.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData(userId = "USER-123")
    }

    fun loadDashboardData(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val sectionResult = repository.getSections(userId)

            sectionResult.onSuccess { sections ->
                val primerPlano = sections.firstOrNull()

                if (primerPlano != null) {
                    val mappingsResult = repository.getDeviceMappings(primerPlano.id)

                    mappingsResult.onSuccess { pines ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            section = primerPlano,
                            mappings = pines
                        )
                    }.onFailure {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            section = primerPlano,
                            errorMessage = "No se pudieron cargar los sensores"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No tienes mapas asignados aún"
                    )
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión al cargar el mapa"
                )
            }
        }
    }

    fun onDeviceClicked(deviceId: String?) {
        if (deviceId == null) {
            println("OSITOPOLAR_DEBUG: 🚨 ALERTA: El pin se tocó, pero su ID es NULO. Revisar el JSON del backend.")
            return
        }

        viewModelScope.launch {
            try {
                println("OSITOPOLAR_DEBUG: Tocaste el pin. Buscando telemetría para: $deviceId")

                _uiState.value = _uiState.value.copy(selectedDeviceId = deviceId)

                // 2. HACEMOS AMBAS PETICIONES DE RED
                val tempResult = repository.getDeviceTemperature(deviceId)
                val humResult = repository.getDeviceHumidity(deviceId)

                println("OSITOPOLAR_DEBUG: Datos recibidos -> Temp: ${tempResult.getOrNull()?.celsius}, Hum: ${humResult.getOrNull()?.percentage}")

                // 3. ACTUALIZAMOS EL ESTADO DE GOLPE
                _uiState.value = _uiState.value.copy(
                    selectedTemperature = tempResult.getOrNull(),
                    selectedHumidity = humResult.getOrNull()
                )

            } catch (e: Exception) {
                println("OSITOPOLAR_DEBUG: CRASH INTERCEPTADO 🚨 -> ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun dismissDeviceDetails() {
        // 4. LIMPIAMOS LA HUMEDAD AL CERRAR LA TARJETA
        _uiState.value = _uiState.value.copy(
            selectedTemperature = null,
            selectedHumidity = null,
            selectedDeviceId = null
        )
    }
}