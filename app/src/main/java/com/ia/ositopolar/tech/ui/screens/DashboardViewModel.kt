package com.ia.ositopolar.tech.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ia.ositopolar.tech.data.remote.NetworkModule
import com.ia.ositopolar.tech.domain.model.Mapping
import com.ia.ositopolar.tech.domain.model.Section
import com.ia.ositopolar.tech.domain.model.Temperature
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
    val selectedDeviceId: String? = null // <--- 1. NUEVA VARIABLE PARA RECORDAR EL ID
)

class DashboardViewModel(
    private val repository: OsitoPolarRepository = NetworkModule.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // En un futuro tomaremos el ID del SessionManager, por ahora cargamos con un valor por defecto
        loadDashboardData(userId = "USER-123")
    }

    private fun loadDashboardData(userId: String) {
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
                println("OSITOPOLAR_DEBUG: Tocaste el pin. Buscando temperatura para: $deviceId")

                // 2. GUARDAMOS EL ID EN EL ESTADO INMEDIATAMENTE
                _uiState.value = _uiState.value.copy(selectedDeviceId = deviceId)

                val tempResult = repository.getDeviceTemperature(deviceId)

                tempResult.onSuccess { temp ->
                    println("OSITOPOLAR_DEBUG: ¡Éxito! Temperatura recibida: ${temp.celsius}")
                    _uiState.value = _uiState.value.copy(selectedTemperature = temp)
                }.onFailure { error ->
                    println("OSITOPOLAR_DEBUG: Falló la respuesta del servidor: ${error.message}")
                }
            } catch (e: Exception) {
                println("OSITOPOLAR_DEBUG: CRASH INTERCEPTADO 🚨 -> ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun dismissDeviceDetails() {
        // 3. LIMPIAMOS EL ID AL CERRAR LA TARJETA
        _uiState.value = _uiState.value.copy(
            selectedTemperature = null,
            selectedDeviceId = null
        )
    }
}