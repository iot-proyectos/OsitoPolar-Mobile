package com.ia.ositopolar.tech.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ia.ositopolar.tech.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onNavigateToWorkOrder: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Fondo oscuro de la aplicación
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // --- HEADER ESTILIZADO ---
        Text(
            text = "Monitoreo en Vivo",
            fontSize = 28.sp,
            color = PolarTextWhite,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Supervisión de cadena de frío",
            fontSize = 14.sp,
            color = PolarTextGray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

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
                    fontWeight = FontWeight.Bold
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
                    .background(PolarSurface) // Fondo azul marino claro
                    .border(1.dp, PolarCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                coordenadas.forEach { pin ->
                    // Nodo IoT en el mapa
                    Box(
                        modifier = Modifier
                            .offset(x = pin.x.dp, y = pin.y.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(StatusRed.copy(alpha = 0.2f)) // Aura de alerta
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

    // --- POPUP (DIALOG) ESTILIZADO DARK MODE ---
    uiState.selectedTemperature?.let { temp ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeviceDetails() },
            containerColor = PolarSurface,
            titleContentColor = PolarTextWhite,
            textContentColor = PolarTextGray,
            title = {
                Text(text = "Alerta Detectada", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Equipo: ${temp.idDevice}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Temperatura: ${temp.celsius}°C",
                        color = StatusRed,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissDeviceDetails()
                        onNavigateToWorkOrder(temp.idDevice)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PolarCyan, contentColor = Color.Black)
                ) {
                    Text("Intervenir", fontWeight = FontWeight.Bold)
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