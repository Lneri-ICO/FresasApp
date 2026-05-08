package com.example.fresasapp.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.fresasapp.R
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5174E))
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_splash),
            contentDescription = "Nay&Jos",
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha),
            contentScale = ContentScale.Crop  // ← Crop para llenar sin bordes
        )
    }
}