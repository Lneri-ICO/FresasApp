package com.example.fresasapp.ui.auth

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fresasapp.viewmodel.AuthState
import com.example.fresasapp.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginExitoso: (String) -> Unit,
    onIrARegistro: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    // Contador de taps en el logo para acceso admin
    var tapCount by remember { mutableStateOf(0) }
    var modoAdmin by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val usuario = (authState as AuthState.Success).usuario
            onLoginExitoso(usuario.rol)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo — toca 5 veces para activar modo admin
        Text(
            text = if (modoAdmin) "🔧" else "🍓",
            fontSize = 64.sp,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures {
                    tapCount++
                    if (tapCount >= 5) {
                        modoAdmin = !modoAdmin
                        tapCount = 0
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (modoAdmin) "Acceso Administrador" else "FresasApp",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = if (modoAdmin) Color(0xFF880E4F) else Color(0xFFE91E63)
        )

        Text(
            text = if (modoAdmin) "Panel de control" else "Inicia sesión para continuar",
            color = Color.Gray
        )

        if (modoAdmin) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color(0xFF880E4F).copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    "🔒 Modo Administrador",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color(0xFF880E4F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (modoAdmin) Color(0xFF880E4F) else Color(0xFFE91E63)
            ),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    if (modoAdmin) "Entrar como Admin" else "Iniciar Sesión",
                    fontSize = 16.sp
                )
            }
        }

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                (authState as AuthState.Error).mensaje,
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!modoAdmin) {
            TextButton(onClick = onIrARegistro) {
                Text("¿No tienes cuenta? Regístrate aquí", color = Color(0xFFE91E63))
            }
        } else {
            TextButton(onClick = {
                modoAdmin = false
                tapCount = 0
            }) {
                Text("← Volver al login de clientes", color = Color.Gray)
            }
        }
    }
}