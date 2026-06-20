package com.campos.atrapamichis.presentation.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campos.atrapamichis.domain.enums.ItemType

@Composable
fun GameScreen(viewModel: GameViewModel) {
    // Observamos el estado del ViewModel (equivalente a un Hook o Signal)
    val state by viewModel.state.collectAsState();

    // Box nos permite apilar elementos en el eje Z (uno sobre otro)
    Box(modifier = Modifier.fillMaxSize()) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF87CEEB)) // Color de cielo temporal
                .onSizeChanged { size ->
                    // Notificamos al ViewModel el tamaño real de la pantalla en píxeles
                    viewModel.updateScreenDimensions(size.width.toFloat(), size.height.toFloat())
                }
                .pointerInput(Unit) {
                    // Sistema de control: Detectamos toques en la pantalla
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val isLeftClick = down.position.x < size.width / 2

                        if (isLeftClick) viewModel.moveLeft(true) else viewModel.moveRight(true)

                        do {
                            val event = awaitPointerEvent()
                        } while (event.changes.any { it.pressed })

                        if (isLeftClick) viewModel.moveLeft(false) else viewModel.moveRight(false)
                    }
                }
        ) {
            // --- ZONA DE DIBUJO ---
            val cat = state.cat

            // 1. Dibujamos al michi (cuadrado naranja)
            // El punto (x, y) de nuestro modelo es el centro, así que calculamos la esquina superior izquierda
            drawRect(
                color = androidx.compose.ui.graphics.Color(0xFFFFA500), // Naranja
                topLeft = Offset(
                    x = cat.position.x - (cat.width / 2f),
                    y = cat.position.y - (cat.height / 2f)
                ),
                size = Size(cat.width, cat.height)
            )

            // 2. Dibujamos los objetos cayendo
            state.fallingItem.forEach { item ->
                // Elegimos un color dependiendo de si es comida o un pepino
                val itemColor = when (item.type) {
                    ItemType.FISH -> Color(0xFF00BCD4) // Azul
                    ItemType.YARN -> Color(0xFFFF69B4) // Rosa
                    ItemType.CUCUMBER -> Color(0xFF008000) // Verde
                }

                drawCircle(
                    color = itemColor,
                    radius = item.radius,
                    center = Offset(x = item.position.x, y = item.position.y)
                )
            }
        }

        // Agregamos una barra superior (HUD) para puntos y vidas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "Puntos: ${state.score}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Vidas: ${state.lives}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        }

        // Pantalla de Game Over si las villas llegan a 0
        if (state.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "¡Estas Muerto!\nPuntos Finales: ${state.score}",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}