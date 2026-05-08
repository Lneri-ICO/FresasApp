package com.example.fresasapp.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fresasapp.data.model.Resena
import com.example.fresasapp.viewmodel.ResenaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResenaScreen(
    productoId: String,
    productoNombre: String,
    onRegresar: () -> Unit,
    viewModel: ResenaViewModel = viewModel()
) {
    val resenas by viewModel.resenas.collectAsState()
    val promedio by viewModel.promedio.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    var calificacion by remember { mutableStateOf(5) }
    var comentario by remember { mutableStateOf("") }
    var mostrarFormulario by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.obtenerResenas(productoId)
    }

    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            kotlinx.coroutines.delay(2000)
            viewModel.limpiarMensaje()
            mostrarFormulario = false
            comentario = ""
            calificacion = 5
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("⭐ Reseñas", fontWeight = FontWeight.Bold)
                        Text(productoNombre, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE91E63),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarFormulario = true },
                containerColor = Color(0xFFE91E63)
            ) {
                Text("✍️", fontSize = 20.sp)
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Resumen de calificaciones
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE91E63).copy(alpha = 0.05f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format("%.1f", promedio),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE91E63)
                        )
                        Row {
                            repeat(5) { index ->
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < promedio.toInt())
                                        Color(0xFFFFC107) else Color.LightGray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text(
                            "${resenas.size} reseñas",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Mensaje confirmación
            if (mensaje.isNotEmpty()) {
                item {
                    Surface(
                        color = Color(0xFF388E3C).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            mensaje,
                            modifier = Modifier.padding(12.dp),
                            color = Color(0xFF388E3C),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (resenas.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⭐", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Sé el primero en reseñar", color = Color.Gray)
                        }
                    }
                }
            } else {
                items(resenas) { resena ->
                    ResenaCard(resena = resena)
                }
            }
        }
    }

    // Diálogo para escribir reseña
    if (mostrarFormulario) {
        AlertDialog(
            onDismissRequest = { mostrarFormulario = false },
            title = { Text("✍️ Escribir reseña") },
            text = {
                Column {
                    Text("Calificación:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Estrellas seleccionables
                    Row {
                        repeat(5) { index ->
                            IconButton(
                                onClick = { calificacion = index + 1 },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < calificacion)
                                        Color(0xFFFFC107) else Color.LightGray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text("Comentario") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.agregarResena(productoId, calificacion, comentario)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    enabled = comentario.isNotEmpty()
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarFormulario = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ResenaCard(resena: Resena) {
    val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        .format(Date(resena.fecha))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    resena.clienteNombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(fecha, color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row {
                repeat(5) { index ->
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < resena.calificacion)
                            Color(0xFFFFC107) else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (resena.comentario.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(resena.comentario, fontSize = 13.sp, color = Color.DarkGray)
            }
        }
    }
}