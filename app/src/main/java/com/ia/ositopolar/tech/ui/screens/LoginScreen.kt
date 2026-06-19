package com.ia.ositopolar.tech.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ia.ositopolar.tech.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit, // <--- Conexión a la pantalla de registro
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- HEADER ---
        Text(
            text = "OsitoPolar",
            fontSize = 36.sp,
            color = PolarCyan,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Gestión de Refrigeración IoT",
            fontSize = 14.sp,
            color = PolarTextGray,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // --- FORMULARIO DE CORREO ---
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.email.value = it },
            label = { Text("Correo electrónico", color = PolarTextGray) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = PolarCyan) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = PolarCyan,
                unfocusedIndicatorColor = PolarSurface,
                focusedTextColor = PolarTextWhite,
                unfocusedTextColor = PolarTextWhite,
                focusedContainerColor = PolarSurface,
                unfocusedContainerColor = PolarSurface,
                cursorColor = PolarCyan
            ),
            singleLine = true
        )

        // --- FORMULARIO DE CONTRASEÑA ---
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Contraseña", color = PolarTextGray) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = PolarCyan) },
            visualTransformation = PasswordVisualTransformation(), // Oculta los caracteres
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = PolarCyan,
                unfocusedIndicatorColor = PolarSurface,
                focusedTextColor = PolarTextWhite,
                unfocusedTextColor = PolarTextWhite,
                focusedContainerColor = PolarSurface,
                unfocusedContainerColor = PolarSurface,
                cursorColor = PolarCyan
            ),
            singleLine = true
        )

        // --- MENSAJE DE ERROR ---
        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = StatusRed,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp),
                fontWeight = FontWeight.Bold
            )
        }

        // --- BOTÓN DE LOGIN ---
        Button(
            onClick = { viewModel.login(onLoginSuccess) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PolarCyan,
                contentColor = Color.Black,
                disabledContainerColor = PolarCyan.copy(alpha = 0.5f)
            ),
            // El botón se bloquea si está cargando o si los campos están vacíos
            enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
            } else {
                Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- IR A REGISTRO ---
        TextButton(onClick = {
            viewModel.clearError() // Limpiamos errores antes de cambiar de pantalla
            onNavigateToRegister()
        }) {
            Text("¿No tienes cuenta? Regístrate", color = PolarCyan)
        }
    }
}