package com.ia.ositopolar.tech.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ia.ositopolar.tech.data.repository.MockOsitoPolarRepository
import com.ia.ositopolar.tech.domain.model.Humidity
import com.ia.ositopolar.tech.domain.model.Temperature
import com.ia.ositopolar.tech.domain.model.WorkOrderRequest // <--- IMPORT NUEVO
import com.ia.ositopolar.tech.domain.repository.OsitoPolarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WorkOrderUiState(
    val isLoadingTelemetry: Boolean = true,
    val isSaving: Boolean = false, // <--- NUEVO ESTADO
    val temperature: Temperature? = null,
    val humidity: Humidity? = null,
    val notes: String = ""
)

class WorkOrderViewModel(
    private val repository: OsitoPolarRepository = MockOsitoPolarRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkOrderUiState())
    val uiState: StateFlow<WorkOrderUiState> = _uiState.asStateFlow()

    fun loadIoTData(deviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTelemetry = true)

            val tempResult = repository.getDeviceTemperature(deviceId)
            val humResult = repository.getDeviceHumidity(deviceId)

            _uiState.value = _uiState.value.copy(
                isLoadingTelemetry = false,
                temperature = tempResult.getOrNull(),
                humidity = humResult.getOrNull()
            )
        }
    }

    fun updateNotes(newNotes: String) {
        _uiState.value = _uiState.value.copy(notes = newNotes)
    }

    // --- NUEVA FUNCIÓN PARA ENVIAR EL REPORTE ---
    fun saveWorkOrder(deviceId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 1. Activamos la animación de carga
            _uiState.value = _uiState.value.copy(isSaving = true)

            // 2. Empaquetamos los datos
            val request = WorkOrderRequest(
                idDevice = deviceId,
                notes = _uiState.value.notes
            )

            // 3. Enviamos al backend (mock)
            val result = repository.saveWorkOrder(request)

            // 4. Apagamos la carga
            _uiState.value = _uiState.value.copy(isSaving = false)

            // 5. Si fue exitoso, regresamos a la pantalla anterior
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
}