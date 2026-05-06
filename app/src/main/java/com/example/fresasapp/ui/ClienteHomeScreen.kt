package com.example.fresasapp.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fresasapp.data.model.Producto
import com.example.fresasapp.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteHomeScreen(
    onIrAlCarrito: (List<Producto>) -> Unit,
    onCerrarSesion: () -> Unit,
    onIrAHistorial: () -> Unit,
    onIrAPerfil: () -> Unit,
    productoViewModel: ProductoViewModel = viewModel()
) {
    val productos by productoViewModel.productos.collectAsState()
    val cargando by productoViewModel.cargando.collectAsState()
    val carrito = remember { mutableStateListOf<Producto>() }
    var busqueda by remember { mutableStateOf("") }
    var buscando by remember { mutableStateOf(false) }

    // Filtrar productos por búsqueda
    val productosFiltrados = productos.filter { it.disponible }.filter { producto ->
        if (busqueda.isEmpty()) true
        else producto.nombre.contains(busqueda, ignoreCase = true) ||
                producto.descripcion.contains(busqueda, ignoreCase = true) ||
                producto.categoria.contains(busqueda, ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        productoViewModel.obtenerProductos()
    }

    Scaffold(
        topBar = {
            if (buscando) {
                // Barra de búsqueda
                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = busqueda,
                            onValueChange = { busqueda = it },
                            placeholder = { Text("Buscar productos...", color = Color.White.copy(alpha = 0.7f)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = Color.White
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            buscando = false
                            busqueda = ""
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFE91E63)
                    )
                )
            } else {
                // Barra normal
                TopAppBar(
                    title = {
                        Column {
                            Text("🍓 FresasApp", fontWeight = FontWeight.Bold)
                            Text(
                                "¿Qué se te antoja hoy?",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFE91E63),
                        titleContentColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = { buscando = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                        }
                        IconButton(onClick = onIrAPerfil) {
                            Text("👤", fontSize = 20.sp)
                        }
                        IconButton(onClick = onIrAHistorial) {
                            Text("📋", fontSize = 20.sp)
                        }
                        BadgedBox(
                            badge = {
                                if (carrito.isNotEmpty()) {
                                    Badge { Text(carrito.size.toString()) }
                                }
                            }
                        ) {
                            IconButton(onClick = { onIrAlCarrito(carrito) }) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Carrito",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                )
            }
        }
    ) { padding ->

        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFE91E63))
            }
        } else if (productosFiltrados.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (busqueda.isEmpty()) "🍓" else "🔍",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        if (busqueda.isEmpty()) "No hay productos disponibles"
                        else "No se encontró \"$busqueda\"",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    if (busqueda.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { busqueda = "" }) {
                            Text("Limpiar búsqueda", color = Color(0xFFE91E63))
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Encabezado con resultados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (busqueda.isEmpty()) "Nuestros Productos"
                        else "Resultados: ${productosFiltrados.size}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (busqueda.isNotEmpty()) {
                        TextButton(onClick = { busqueda = "" }) {
                            Text("Limpiar", color = Color(0xFFE91E63))
                        }
                    }
                }

                // Chips de categorías
                if (busqueda.isEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Todos", "fresas", "carlota", "vaso_pay").forEach { cat ->
                            FilterChip(
                                selected = busqueda == cat || (cat == "Todos" && busqueda.isEmpty()),
                                onClick = {
                                    busqueda = if (cat == "Todos") "" else cat
                                },
                                label = {
                                    Text(
                                        when (cat) {
                                            "Todos" -> "🍽️ Todos"
                                            "fresas" -> "🍓 Fresas"
                                            "carlota" -> "🍋 Carlota"
                                            "vaso_pay" -> "🥧 Pay"
                                            else -> cat
                                        },
                                        fontSize = 12.sp
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productosFiltrados) { producto ->
                        ProductoCard(
                            producto = producto,
                            onAgregar = { carrito.add(producto) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoCard(producto: Producto, onAgregar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (producto.categoria) {
                    "fresas" -> "🍓"
                    "carlota" -> "🍋"
                    "vaso_pay" -> "🥧"
                    else -> "🍮"
                },
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(producto.descripcion, fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "$${producto.precio}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E63),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAgregar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Agregar", fontSize = 12.sp)
            }
        }
    }
}