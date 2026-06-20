package com.ia.ositopolar.tech.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import com.ia.ositopolar.tech.ui.theme.*
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun TelemetryWidget(deviceId: String, temperature: Float?, humidity: Float?) {
    // Contenedor principal (La tarjeta azul marino con bordes muy redondeados)
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PolarSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            // --- HEADER: Icono, Título y Etiqueta de Estado ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icono celeste cuadrado con bordes redondeados
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PolarCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Build, contentDescription = "Icono de mantenimiento", tint = Color.Cyan)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Equipo $deviceId", color = PolarTextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Supermercado Central · Sector B", color = PolarTextGray, fontSize = 12.sp)
                    }
                }

                // Etiqueta "En línea" (Píldora verde)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(StatusGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusGreen))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "En línea", color = StatusGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- BLOQUE DE MÉTRICAS (Temperatura y Humedad) ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                // Caja de Temperatura (Estilo oscuro interno)
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(PolarNavy).padding(16.dp)) {
                    Column {
                        Text(text = "TEMPERATURA", color = PolarTextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text(text = "${temperature ?: "--"}°", color = PolarCyan, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "Celsius", color = PolarTextGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "↓ Normal", color = StatusGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }

                // Caja de Humedad
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(PolarNavy).padding(16.dp)) {
                    Column {
                        Text(text = "HUMEDAD", color = PolarTextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text(text = "${humidity ?: "--"}%", color = PolarCyan, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "Relativa", color = PolarTextGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "↑ Vigilar", color = StatusYellow, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- CAJA DE ALERTA SUGERIDA ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PolarNavy)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Warning, contentDescription = "Alerta", tint = StatusYellow, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Humedad en límite — revisión preventiva sugerida",
                    color = PolarTextWhite,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(text = "2m", color = PolarTextGray, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkOrderScreen(
    deviceId: String,
    onBackClick: () -> Unit,
    viewModel: WorkOrderViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- Estado para guardar la ruta de la imagen ---
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // --- Lanzador del PhotoPicker nativo de Android ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    LaunchedEffect(deviceId) {
        viewModel.loadIoTData(deviceId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // --- HEADER ACTUALIZADO CON FLECHA DE REGRESO ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver al mapa",
                    tint = PolarCyan
                )
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "Orden de Trabajo",
                    fontSize = 28.sp,
                    color = PolarTextWhite,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Registro de mantenimiento y diagnóstico",
                    fontSize = 14.sp,
                    color = PolarTextGray
                )
            }
        }

        if (uiState.isLoadingTelemetry) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PolarCyan)
            }
        } else {
            TelemetryWidget(
                deviceId = deviceId,
                temperature = uiState.temperature?.celsius,
                humidity = uiState.humidity?.percentage
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.notes,
            onValueChange = { viewModel.updateNotes(it) },
            label = { Text("Diagnóstico del técnico...", color = PolarTextGray) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = PolarCyan,
                unfocusedIndicatorColor = PolarSurface,
                focusedTextColor = PolarTextWhite,
                unfocusedTextColor = PolarTextWhite,
                focusedContainerColor = PolarSurface.copy(alpha = 0.5f),
                unfocusedContainerColor = PolarSurface.copy(alpha = 0.5f),
                cursorColor = PolarCyan
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECCIÓN DE EVIDENCIA FOTOGRÁFICA ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón secundario para abrir la galería
            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PolarCyan),
                border = BorderStroke(1.dp, PolarCyan)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Subir foto")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adjuntar Evidencia")
            }

            // Previsualización de la imagen seleccionada
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Evidencia seleccionada",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, PolarSurface, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(text = "Sin imagen", color = PolarTextGray, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- BOTÓN PRINCIPAL INTELIGENTE ---
        Button(
            onClick = {
                viewModel.saveWorkOrder(
                    deviceId = deviceId,
                    onSuccess = onBackClick
                )
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PolarCyan,
                contentColor = Color.Black,
                disabledContainerColor = PolarCyan.copy(alpha = 0.5f)
            ),
            enabled = !uiState.isSaving
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    color = Color.Black,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp
                )
            } else {
                Text("Guardar Reporte", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}