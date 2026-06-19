package com.ia.ositopolar.tech.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val name by viewModel.name.collectAsState()
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
        Text(text = "Crear Cuenta", fontSize = 32.sp, color = PolarTextWhite, fontWeight = FontWeight.ExtraBold)
        Text(text = "Únete a OsitoPolar", fontSize = 14.sp, color = PolarTextGray, modifier = Modifier.padding(bottom = 32.dp))

        // --- CAMPO NOMBRE ---
        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.name.value = it },
            label = { Text("Nombre completo", color = PolarTextGray) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = PolarCyan) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = PolarCyan, unfocusedIndicatorColor = PolarSurface,
                focusedTextColor = PolarTextWhite, unfocusedTextColor = PolarTextWhite,
                focusedContainerColor = PolarSurface, unfocusedContainerColor = PolarSurface
            ),
            singleLine = true
        )

        // --- CAMPO CORREO ---
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.email.value = it },
            label = { Text("Correo electrónico", color = PolarTextGray) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = PolarCyan) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = PolarCyan, unfocusedIndicatorColor = PolarSurface,
                focusedTextColor = PolarTextWhite, unfocusedTextColor = PolarTextWhite,
                focusedContainerColor = PolarSurface, unfocusedContainerColor = PolarSurface
            ),
            singleLine = true
        )

        // --- CAMPO CONTRASEÑA ---
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Contraseña", color = PolarTextGray) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = PolarCyan) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = PolarCyan, unfocusedIndicatorColor = PolarSurface,
                focusedTextColor = PolarTextWhite, unfocusedTextColor = PolarTextWhite,
                focusedContainerColor = PolarSurface, unfocusedContainerColor = PolarSurface
            ),
            singleLine = true
        )

        if (uiState.error != null) {
            Text(text = uiState.error!!, color = StatusRed, fontSize = 14.sp, modifier = Modifier.padding(bottom = 16.dp), fontWeight = FontWeight.Bold)
        }

        // --- BOTÓN REGISTRAR ---
        Button(
            onClick = { viewModel.register(onRegisterSuccess) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PolarCyan, contentColor = Color.Black, disabledContainerColor = PolarCyan.copy(alpha = 0.5f)),
            enabled = !uiState.isLoading && name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
            } else {
                Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- IR AL LOGIN ---
        TextButton(onClick = {
            viewModel.clearError()
            onNavigateToLogin()
        }) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = PolarCyan)
        }
    }
}