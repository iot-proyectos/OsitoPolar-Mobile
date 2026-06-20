package com.ia.ositopolar.tech.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ia.ositopolar.tech.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onNavigateToWorkOrder: (String) -> Unit,
    onNavigateToManagement: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Envolvemos todo en un Box para poder superponer el botón flotante
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo oscuro y contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // --- HEADER ESTILIZADO (ÚNICO) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Monitoreo en Vivo",
                        fontSize = 28.sp,
                        color = PolarTextWhite,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Supervisión de cadena de frío",
                        fontSize = 14.sp,
                        color = PolarTextGray
                    )
                }

                IconButton(onClick = { onNavigateToManagement() }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Gestión de Equipos",
                        tint = PolarCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PolarCyan)
                }
            } else if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage!!, color = StatusRed)
            } else {
                val plano = uiState.section
                val coordenadas = uiState.mappings

                // --- INFO DE LA SECCIÓN ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sector: ${plano?.id}",
                        color = PolarCyan,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = "${coordenadas.size} equipos activos",
                        color = PolarTextGray,
                        fontSize = 12.sp
                    )
                }

                // --- EL MAPA INDOOR (PLANO) ESTILIZADO ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PolarSurface)
                        .border(1.dp, PolarCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    coordenadas.forEach { pin ->
                        Box(
                            modifier = Modifier
                                .offset(x = pin.x.dp, y = pin.y.dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(StatusRed.copy(alpha = 0.2f))
                                .clickable { viewModel.onDeviceClicked(pin.idDevice) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Dispositivo ${pin.idDevice}",
                                tint = StatusRed,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- NUEVO: BOTÓN FLOTANTE DE RECARGA GLOBAL ---
        FloatingActionButton(
            onClick = {
                viewModel.loadDashboardData(userId = "USER-123")
            },
            containerColor = PolarCyan,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Recargar Mapa",
                tint = Color(0xFF0F172A)
            )
        }
    }

    // --- POPUP (DIALOG) ---
    uiState.selectedDeviceId?.let { deviceId ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeviceDetails() },
            containerColor = PolarSurface,
            titleContentColor = PolarTextWhite,
            textContentColor = PolarTextGray,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Lectura del Equipo", fontWeight = FontWeight.Bold)

                    IconButton(
                        onClick = { viewModel.onDeviceClicked(deviceId) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar datos",
                            tint = PolarCyan
                        )
                    }
                }
            },
            text = {
                Column {
                    Text("ID: $deviceId", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    val temp = uiState.selectedTemperature
                    if (temp != null) {
                        Text(
                            text = "Temperatura: ${temp.celsius}°C",
                            color = StatusRed,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    } else {
                        Text("Temperatura: Cargando...", color = PolarTextGray)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val hum = uiState.selectedHumidity
                    if (hum != null) {
                        Text(
                            text = "Humedad: ${hum.percentage}%",
                            color = PolarCyan,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    } else {
                        Text("Humedad: Cargando...", color = PolarTextGray)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onNavigateToWorkOrder(deviceId) }) {
                    Text("Intervenir", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeviceDetails() }) {
                    Text("Cerrar", color = PolarTextGray)
                }
            }
        )
    }
}