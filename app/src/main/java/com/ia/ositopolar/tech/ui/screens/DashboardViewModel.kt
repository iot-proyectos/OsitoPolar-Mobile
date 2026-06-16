package com.ia.ositopolar.tech.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ia.ositopolar.tech.data.repository.MockOsitoPolarRepository
import com.ia.ositopolar.tech.domain.model.Mapping
import com.ia.ositopolar.tech.domain.model.Section
import com.ia.ositopolar.tech.domain.model.Temperature // <-- Este era el import faltante
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
    val selectedTemperature: Temperature? = null
)

class DashboardViewModel(
    private val repository: OsitoPolarRepository = MockOsitoPolarRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData(userId = "USER-123")
    }

    private fun loadDashboardData(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val sectionResult = repository.getSections(userId)

            sectionResult.onSuccess { sections ->
                val primerPlano = sections.firstOrNull()

                if (primerPlano != null) {
                    val mappingsResult = repository.getDeviceMappings(primerPlano.id)

                    mappingsResult.onSuccess { pines ->
                        // CAMBIAMOS = DashboardUiState(...) POR = _uiState.value.copy(...)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            section = primerPlano,
                            mappings = pines
                        )
                    }
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar el mapa"
                )
            }
        }
    }

    fun onDeviceClicked(deviceId: String) {
        viewModelScope.launch {
            val tempResult = repository.getDeviceTemperature(deviceId)

            tempResult.onSuccess { temp ->
                _uiState.value = _uiState.value.copy(selectedTemperature = temp)
            }
        }
    }

    fun dismissDeviceDetails() {
        _uiState.value = _uiState.value.copy(selectedTemperature = null)
    }
}