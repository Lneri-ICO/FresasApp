package com.example.fresasapp.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fresasapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashTerminado: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(2500)
        onSplashTerminado()
    }

    // El fondo es del mismo color rosa del logo para que se vea uniforme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF52E65)), // mismo rosa del logo
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_splash),
            contentDescription = "Nay&Jos",
            modifier = Modifier
                .fillMaxWidth()
                .alpha(alpha),
            contentScale = ContentScale.FillWidth
        )
    }
}