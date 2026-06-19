package com.ia.ositopolar.tech.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ia.ositopolar.tech.data.remote.LoginRequest
import com.ia.ositopolar.tech.data.remote.NetworkModule
import com.ia.ositopolar.tech.data.remote.RegisterRequest
import com.ia.ositopolar.tech.data.remote.SessionManager // <--- IMPORTANTE: Importamos tu billetera de sesión
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val api = NetworkModule.api

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    var name = MutableStateFlow("")
    var email = MutableStateFlow("")
    var password = MutableStateFlow("")

    private fun parseErrorMessage(errorBody: okhttp3.ResponseBody?): String {
        return try {
            val jsonString = errorBody?.string()
            if (jsonString != null) {
                val jsonObject = JSONObject(jsonString)
                jsonObject.getJSONObject("error").getString("message")
            } else {
                "Error desconocido del servidor"
            }
        } catch (e: Exception) {
            "Error al procesar la respuesta"
        }
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val request = LoginRequest(email.value, password.value)
                val response = api.login(request)

                if (response.isSuccessful && response.body()?.success == true) {

                    // --- ATRAPAMOS EL TOKEN ---
                    // Dependiendo de cómo armaste tu ApiResponse.kt, el token suele venir dentro de 'data'
                    val token = response.body()?.data?.token
                        ?: response.body()?.data?.accessToken // Por si tu backend lo llama accessToken

                    if (token != null) {
                        SessionManager.token = token
                    }
                    // --------------------------

                    _uiState.value = AuthUiState(isSuccess = true)
                    onSuccess()
                } else {
                    val errorMsg = parseErrorMessage(response.errorBody())
                    _uiState.value = AuthUiState(error = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = "Error de conexión: ${e.message}")
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val request = RegisterRequest(name.value, email.value, password.value)
                val response = api.register(request)

                if (response.isSuccessful && response.body()?.success == true) {

                    // --- ATRAPAMOS EL TOKEN TAMBIÉN AL REGISTRAR ---
                    val token = response.body()?.data?.token
                        ?: response.body()?.data?.accessToken

                    if (token != null) {
                        SessionManager.token = token
                    }
                    // -----------------------------------------------

                    _uiState.value = AuthUiState(isSuccess = true)
                    onSuccess()
                } else {
                    val errorMsg = parseErrorMessage(response.errorBody())
                    _uiState.value = AuthUiState(error = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = "Error de conexión: ${e.message}")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}