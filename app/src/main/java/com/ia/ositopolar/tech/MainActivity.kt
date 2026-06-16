package com.ia.ositopolar.tech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ia.ositopolar.tech.ui.screens.DashboardScreen
import com.ia.ositopolar.tech.ui.screens.WorkOrderScreen
import com.ia.ositopolar.tech.ui.theme.OsitoPolarTecnicoViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OsitoPolarTecnicoViewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Creamos el controlador de navegación
                    val navController = rememberNavController()

                    // 2. Definimos las rutas de la app
                    NavHost(navController = navController, startDestination = "dashboard") {

                        // Ruta 1: El Mapa
                        composable("dashboard") {
                            DashboardScreen(
                                onNavigateToWorkOrder = { deviceId ->
                                    // Cuando le den al botón del popup, navegamos a "work_order/DEV-001"
                                    navController.navigate("work_order/$deviceId")
                                }
                            )
                        }

                        // Ruta 2: La Orden de Trabajo
                        composable("work_order/{deviceId}") { backStackEntry ->
                            // Extraemos el ID del dispositivo de la ruta
                            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""

                            WorkOrderScreen(
                                deviceId = deviceId,
                                onBackClick = {
                                    // Regresamos al mapa
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}