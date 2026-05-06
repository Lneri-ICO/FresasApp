package com.example.fresasapp.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fresasapp.data.model.Producto
import com.example.fresasapp.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductosScreen(
    onRegresar: () -> Unit,
    viewModel: ProductoViewModel = viewModel()
) {
    val productos by viewModel.productos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    var mostrarDialogo by remember { mutableStateOf(false) }
    var productoEditar by remember { mutableStateOf<Producto?>(null) }

    LaunchedEffect(Unit) {
        viewModel.obtenerProductos()
    }

    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            kotlinx.coroutines.delay(2000)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛍️ Gestión de Productos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF880E4F),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    productoEditar = null
                    mostrarDialogo = true
                },
                containerColor = Color(0xFF880E4F)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
            }
        }
    ) { padding ->

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Mensaje de confirmación
            if (mensaje.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF388E3C)
                ) {
                    Text(
                        mensaje,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF880E4F))
                }
            } else if (productos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🍓", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No hay productos", fontSize = 18.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Toca el botón + para agregar", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productos) { producto ->
                        ProductoAdminCard(
                            producto = producto,
                            onEditar = {
                                productoEditar = producto
                                mostrarDialogo = true
                            },
                            onEliminar = { viewModel.eliminarProducto(producto.id) },
                            onToggleDisponibilidad = { viewModel.toggleDisponibilidad(producto) }
                        )
                    }
                }
            }
        }
    }

    // Diálogo para agregar/editar producto
    if (mostrarDialogo) {
        DialogProducto(
            producto = productoEditar,
            onGuardar = { producto ->
                if (productoEditar == null) {
                    viewModel.agregarProducto(producto)
                } else {
                    viewModel.actualizarProducto(producto)
                }
                mostrarDialogo = false
            },
            onCerrar = { mostrarDialogo = false }
        )
    }
}

@Composable
fun ProductoAdminCard(
    producto: Producto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onToggleDisponibilidad: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (producto.categoria) {
                    "fresas" -> "🍓"
                    "carlota" -> "🍋"
                    "vaso_pay" -> "🥧"
                    else -> "🍮"
                },
                fontSize = 36.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(producto.descripcion, fontSize = 12.sp, color = Color.Gray)
                Text("$${producto.precio}", color = Color(0xFF880E4F), fontWeight = FontWeight.Bold)
                Text(
                    if (producto.disponible) "✅ Disponible" else "❌ No disponible",
                    fontSize = 12.sp,
                    color = if (producto.disponible) Color(0xFF388E3C) else Color.Red
                )
            }

            Column {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF880E4F))
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
                Switch(
                    checked = producto.disponible,
                    onCheckedChange = { onToggleDisponibilidad() },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF880E4F))
                )
            }
        }
    }
}

@Composable
fun DialogProducto(
    producto: Producto?,
    onGuardar: (Producto) -> Unit,
    onCerrar: () -> Unit
) {
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(producto?.descripcion ?: "") }
    var precio by remember { mutableStateOf(producto?.precio?.toString() ?: "") }
    var categoria by remember { mutableStateOf(producto?.categoria ?: "fresas") }

    val categorias = listOf("fresas", "carlota", "vaso_pay")

    Dialog(onDismissRequest = onCerrar) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    if (producto == null) "➕ Nuevo Producto" else "✏️ Editar Producto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF880E4F)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Categoría:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categorias.forEach { cat ->
                        FilterChip(
                            selected = categoria == cat,
                            onClick = { categoria = cat },
                            label = {
                                Text(
                                    when (cat) {
                                        "fresas" -> "🍓 Fresas"
                                        "carlota" -> "🍋 Carlota"
                                        "vaso_pay" -> "🥧 Vaso Pay"
                                        else -> cat
                                    },
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCerrar,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val precioDouble = precio.toDoubleOrNull() ?: 0.0
                            val nuevoProducto = Producto(
                                id = producto?.id ?: "",
                                nombre = nombre,
                                descripcion = descripcion,
                                precio = precioDouble,
                                categoria = categoria,
                                disponible = producto?.disponible ?: true
                            )
                            onGuardar(nuevoProducto)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF880E4F)),
                        enabled = nombre.isNotEmpty() && precio.isNotEmpty()
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}