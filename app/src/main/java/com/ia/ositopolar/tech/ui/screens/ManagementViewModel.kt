package com.ia.ositopolar.tech.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ia.ositopolar.tech.data.remote.NetworkModule
import com.ia.ositopolar.tech.domain.repository.OsitoPolarRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// El estado que observará la pantalla
data class ManagementUiState(
    val deviceName: String = "",
    val serialNumber: String = "",
    val coordinateX: String = "",
    val coordinateY: String = "",
    val isSubmitting: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val history: List<MockReading> = emptyList()
)

class ManagementViewModel(
    private val repository: OsitoPolarRepository = NetworkModule.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagementUiState())
    val uiState: StateFlow<ManagementUiState> = _uiState.asStateFlow()

    init {
        // Al abrir la pantalla, pedimos a la BD que traiga el historial
        loadHistory()
    }

    // Funciones para actualizar el texto mientras el usuario escribe
    fun onDeviceNameChange(newValue: String) { _uiState.value = _uiState.value.copy(deviceName = newValue) }
    fun onSerialNumberChange(newValue: String) { _uiState.value = _uiState.value.copy(serialNumber = newValue) }
    fun onCoordinateXChange(newValue: String) { _uiState.value = _uiState.value.copy(coordinateX = newValue) }
    fun onCoordinateYChange(newValue: String) { _uiState.value = _uiState.value.copy(coordinateY = newValue) }

    fun submitDevice() {
        val state = _uiState.value

        // Validación básica
        if (state.deviceName.isBlank() || state.serialNumber.isBlank() || state.coordinateX.isBlank() || state.coordinateY.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Por favor, completa todos los campos.")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isSubmitting = true, errorMessage = null, successMessage = null)

            try {
                // 1. Convertimos los textos a números para las coordenadas
                val x = state.coordinateX.toFloatOrNull() ?: 0f
                val y = state.coordinateY.toFloatOrNull() ?: 0f

                // 2. HACEMOS LA LLAMADA REAL A LA BASE DE DATOS
                val result = repository.createDevice(
                    name = state.deviceName,
                    serialNumber = state.serialNumber,
                    x = x,
                    y = y
                )

                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        successMessage = "¡Equipo guardado exitosamente en la Base de Datos!",
                        deviceName = "",
                        serialNumber = "",
                        coordinateX = "",
                        coordinateY = ""
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        errorMessage = "El servidor rechazó la petición: ${error.message}"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun dismissMessages() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }

    private fun loadHistory() {
        // AQUÍ VA LA PETICIÓN AL BACKEND PARA TRAER LAS ÚLTIMAS TEMPERATURAS
        // val historyResult = repository.getDeviceHistory(...)

        // Simulación temporal para la UI
        val dataDeLaBD = listOf(
            MockReading("Hoy, 19:45", "5.0°C", "60.0%", isAlert = true),
            MockReading("Hoy, 19:30", "-4.5°C", "65.5%", isAlert = false),
            MockReading("Hoy, 19:15", "-4.2°C", "64.0%", isAlert = false)
        )
        _uiState.value = _uiState.value.copy(history = dataDeLaBD)
    }
}