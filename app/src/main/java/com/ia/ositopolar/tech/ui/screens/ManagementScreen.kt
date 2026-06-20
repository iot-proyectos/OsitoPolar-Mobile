package com.ia.ositopolar.tech.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ia.ositopolar.tech.ui.theme.*

data class MockReading(
    val time: String,
    val temp: String,
    val hum: String,
    val isAlert: Boolean
)

@Composable
fun ManagementScreen(
    viewModel: ManagementViewModel = viewModel(), // <--- Inyectamos el ViewModel
    onNavigateBack: () -> Unit
) {
    // Escuchamos los datos que vienen del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {

        // --- HEADER ---
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver", tint = PolarCyan)
                }
                Text(text = "Gestión de Equipos", fontSize = 24.sp, color = PolarTextWhite, fontWeight = FontWeight.Bold)
            }
        }

        // --- FORMULARIO DE REGISTRO ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PolarSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Registrar Nuevo Sensor",
                        fontSize = 18.sp, color = PolarCyan, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = uiState.deviceName, // <--- Conectado a la BD/ViewModel
                        onValueChange = { viewModel.onDeviceNameChange(it) },
                        label = { Text("Nombre del Equipo") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PolarTextWhite, unfocusedTextColor = PolarTextWhite,
                            focusedBorderColor = PolarCyan, unfocusedLabelColor = PolarTextGray
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.serialNumber,
                        onValueChange = { viewModel.onSerialNumberChange(it) },
                        label = { Text("Número de Serie") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PolarTextWhite, unfocusedTextColor = PolarTextWhite,
                            focusedBorderColor = PolarCyan, unfocusedLabelColor = PolarTextGray
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.coordinateX, onValueChange = { viewModel.onCoordinateXChange(it) },
                            label = { Text("Eje X") }, modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PolarTextWhite, unfocusedTextColor = PolarTextWhite,
                                focusedBorderColor = PolarCyan, unfocusedLabelColor = PolarTextGray
                            )
                        )
                        OutlinedTextField(
                            value = uiState.coordinateY, onValueChange = { viewModel.onCoordinateYChange(it) },
                            label = { Text("Eje Y") }, modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PolarTextWhite, unfocusedTextColor = PolarTextWhite,
                                focusedBorderColor = PolarCyan, unfocusedLabelColor = PolarTextGray
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de Guardar con estado de carga
                    Button(
                        onClick = { viewModel.submitDevice() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = PolarCyan, disabledContainerColor = PolarTextGray)
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(color = PolarSurface, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Crear y Asignar al Mapa", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Mensajes de Alerta
            if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage!!, color = StatusRed, modifier = Modifier.padding(top = 8.dp))
            }
            if (uiState.successMessage != null) {
                Text(text = uiState.successMessage!!, color = PolarCyan, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- TÍTULO DEL HISTORIAL ---
        item {
            Text(
                text = "Últimas Lecturas",
                fontSize = 18.sp, color = PolarTextWhite, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
            )
        }

        // --- LISTA DEL HISTORIAL DESDE LA BD ---
        items(uiState.history) { reading ->
            Card(
                colors = CardDefaults.cardColors(containerColor = PolarSurface),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = reading.time, color = PolarTextGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = reading.temp,
                                color = if (reading.isAlert) StatusRed else PolarTextWhite,
                                fontWeight = FontWeight.Bold, fontSize = 18.sp
                            )
                            Text(text = "  |  ${reading.hum}", color = PolarCyan, fontSize = 14.sp)
                        }
                    }
                    if (reading.isAlert) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Alerta", tint = StatusRed, modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }
}