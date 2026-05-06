package com.example.fresasapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fresasapp.data.model.Producto
import com.example.fresasapp.ui.SplashScreen
import com.example.fresasapp.ui.admin.AdminEstadisticasScreen
import com.example.fresasapp.ui.admin.AdminHomeScreen
import com.example.fresasapp.ui.admin.AdminProductosScreen
import com.example.fresasapp.ui.auth.LoginScreen
import com.example.fresasapp.ui.auth.RegistroScreen
import com.example.fresasapp.ui.client.CarritoScreen
import com.example.fresasapp.ui.client.ClienteHomeScreen
import com.example.fresasapp.ui.client.ConfirmacionScreen
import com.example.fresasapp.ui.client.HistorialScreen
import com.example.fresasapp.ui.client.PerfilScreen
import com.example.fresasapp.ui.theme.FresasAppTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private fun pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pedirPermisoNotificaciones()

        setContent {
            FresasAppTheme {
                val navController = rememberNavController()
                val carritoItems = remember { mutableStateListOf<Producto>() }

                NavHost(navController = navController, startDestination = "splash") {

                    composable("splash") {
                        SplashScreen(
                            onSplashTerminado = {
                                navController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("login") {
                        LoginScreen(
                            onLoginExitoso = { rol ->
                                if (rol == "admin") {
                                    navController.navigate("admin_home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("cliente_home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            },
                            onIrARegistro = { navController.navigate("registro") }
                        )
                    }

                    composable("registro") {
                        RegistroScreen(
                            onRegistroExitoso = {
                                navController.navigate("login") {
                                    popUpTo("registro") { inclusive = true }
                                }
                            },
                            onIrALogin = { navController.popBackStack() }
                        )
                    }

                    composable("cliente_home") {
                        ClienteHomeScreen(
                            onIrAlCarrito = { items ->
                                carritoItems.clear()
                                carritoItems.addAll(items)
                                navController.navigate("carrito")
                            },
                            onCerrarSesion = {
                                navController.navigate("login") {
                                    popUpTo("cliente_home") { inclusive = true }
                                }
                            },
                            onIrAHistorial = {
                                navController.navigate("historial")
                            },
                            onIrAPerfil = {
                                navController.navigate("perfil")
                            }
                        )
                    }

                    composable("perfil") {
                        PerfilScreen(
                            onRegresar = { navController.popBackStack() }
                        )
                    }

                    composable("carrito") {
                        CarritoScreen(
                            carrito = carritoItems,
                            onEliminar = { producto -> carritoItems.remove(producto) },
                            onConfirmarPedido = { tipoEntrega ->
                                navController.navigate("confirmacion/$tipoEntrega")
                            },
                            onRegresar = { navController.popBackStack() }
                        )
                    }

                    composable("confirmacion/{tipoEntrega}") { backStackEntry ->
                        val tipoEntrega =
                            backStackEntry.arguments?.getString("tipoEntrega") ?: "recoger"
                        ConfirmacionScreen(
                            carrito = carritoItems,
                            tipoEntrega = tipoEntrega,
                            onIrAlInicio = {
                                carritoItems.clear()
                                navController.navigate("cliente_home") {
                                    popUpTo("cliente_home") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("historial") {
                        HistorialScreen(
                            onRegresar = { navController.popBackStack() }
                        )
                    }

                    composable("admin_home") {
                        AdminHomeScreen(
                            onCerrarSesion = {
                                navController.navigate("login") {
                                    popUpTo("admin_home") { inclusive = true }
                                }
                            },
                            onIrAProductos = {
                                navController.navigate("admin_productos")
                            },
                            onIrAEstadisticas = {
                                navController.navigate("admin_estadisticas")
                            }
                        )
                    }

                    composable("admin_productos") {
                        AdminProductosScreen(
                            onRegresar = { navController.popBackStack() }
                        )
                    }

                    composable("admin_estadisticas") {
                        AdminEstadisticasScreen(
                            onRegresar = { navController.popBackStack() }
                        )
                    }

                }
            }
        }
    }
}