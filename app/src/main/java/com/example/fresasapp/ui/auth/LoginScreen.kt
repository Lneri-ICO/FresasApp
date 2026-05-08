package com.example.fresasapp.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fresasapp.R
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
    var tapCount by remember { mutableStateOf(0) }
    var modoAdmin by remember { mutableStateOf(false) }
    var mostrarDialogoReset by remember { mutableStateOf(false) }
    var emailReset by remember { mutableStateOf("") }
    var mensajeReset by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val usuario = (authState as AuthState.Success).usuario
            onLoginExitoso(usuario.rol)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (modoAdmin) {
                Spacer(modifier = Modifier.height(80.dp))
                Text(
                    text = "🔧",
                    fontSize = 64.sp,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures {
                            tapCount++
                            if (tapCount >= 5) {
                                modoAdmin = false
                                tapCount = 0
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Acceso Administrador",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF880E4F)
                )
                Text(text = "Panel de control", color = Color.Gray)
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
            } else {
                // Logo más compacto verticalmente
                Image(
                    painter = painterResource(id = R.drawable.logo_login),
                    contentDescription = "Nay&Jos Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.65f)  // no ocupa todo el ancho
                        .padding(top = 48.dp) // pequeño espacio arriba
                        .pointerInput(Unit) {
                            detectTapGestures {
                                tapCount++
                                if (tapCount >= 5) {
                                    modoAdmin = true
                                    tapCount = 0
                                }
                            }
                        },
                    contentScale = ContentScale.FillWidth
                )
            }

            // Formulario pegado al logo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!modoAdmin) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Inicia sesión para continuar",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (modoAdmin) Color(0xFF880E4F) else Color(0xFFE91E63)
                    ),
                    enabled = authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            if (modoAdmin) "Entrar como Admin" else "Iniciar Sesión",
                            fontSize = 16.sp
                        )
                    }
                }

                if (authState is AuthState.Error) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        (authState as AuthState.Error).mensaje,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }

                TextButton(onClick = { mostrarDialogoReset = true }) {
                    Text("¿Olvidaste tu contraseña?", color = Color.Gray, fontSize = 13.sp)
                }

                if (!modoAdmin) {
                    TextButton(onClick = onIrARegistro) {
                        Text(
                            "¿No tienes cuenta? Regístrate aquí",
                            color = Color(0xFFE91E63)
                        )
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
    }

    if (mostrarDialogoReset) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoReset = false },
            title = { Text("Recuperar contraseña") },
            text = {
                Column {
                    Text("Ingresa tu correo y te enviaremos un link para restablecer tu contraseña.")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = emailReset,
                        onValueChange = { emailReset = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (mensajeReset.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            mensajeReset,
                            color = if (mensajeReset.contains("✅"))
                                Color(0xFF388E3C) else Color.Red,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (emailReset.isNotEmpty()) {
                            com.google.firebase.auth.FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(emailReset)
                                .addOnSuccessListener {
                                    mensajeReset = "✅ Correo enviado, revisa tu bandeja"
                                }
                                .addOnFailureListener {
                                    mensajeReset = "❌ Error: ${it.message}"
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoReset = false
                    mensajeReset = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}