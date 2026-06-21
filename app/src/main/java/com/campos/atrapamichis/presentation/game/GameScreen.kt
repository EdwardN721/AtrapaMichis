package com.campos.atrapamichis.presentation.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campos.atrapamichis.R
import com.campos.atrapamichis.domain.enums.CatEmotion
import com.campos.atrapamichis.domain.enums.ItemType

@Composable
fun GameScreen(viewModel: GameViewModel) {
    // Observamos el estado del ViewModel (equivalente a un Hook o Signal)
    val state by viewModel.state.collectAsState()

    // 1. Cargar imagenes
    val catNormalImage = ImageBitmap.imageResource(id = R.drawable.cat)
    val catHappyImage = ImageBitmap.imageResource(id = R.drawable.cat_happy)
    val catAngryImage = ImageBitmap.imageResource(id = R.drawable.cat_angry)
    val fishImage = ImageBitmap.imageResource(id = R.drawable.fish)
    val yarnImage = ImageBitmap.imageResource(id = R.drawable.yarn)
    val cucumberImage = ImageBitmap.imageResource(id = R.drawable.cucumber)

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

            // Elige la cara a dibujar según su estado
            val imageToDraw = when(state.catEmotion){
                CatEmotion.NORMAL -> catNormalImage
                CatEmotion.HAPPY -> catHappyImage
                CatEmotion.ANGRY -> catAngryImage
            }

            // 1. Dibujar al gato con la imagen cargada
            drawImage(
                image = imageToDraw,
                dstOffset = IntOffset(
                    x = (cat.position.x - (cat.width / 2f)).toInt(),
                    y = (cat.position.y - (cat.height / 2f)).toInt()
                ),
                dstSize = IntSize(cat.width.toInt(), cat.height.toInt())
            )

            // 2. Dibujamos los objetos cayendo
            state.fallingItem.forEach { item ->
                // Elegimos la imagen correspondiente
                val imageToDraw = when (item.type) {
                    ItemType.FISH -> fishImage // Pescadp
                    ItemType.YARN -> yarnImage // Estambre
                    ItemType.CUCUMBER -> cucumberImage // Pepino
                }

                // El radio determina el tamaño total de la imagen (diámetro = radio * 2)
                val imageSize = (item.radius * 2).toInt()

                drawImage(
                    image = imageToDraw,
                    dstOffset = IntOffset(
                        x = (item.position.x - item.radius).toInt(),
                        y = (item.position.y - item.radius).toInt()
                    ),
                    dstSize = IntSize(imageSize, imageSize)
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
            // Usamos una columna para apilar los Puntos y el Récord del lado izquierdo
            Column {
                Text(
                    text = "Puntos: ${state.score}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Récord: ${state.highScore}", // ¡Aquí mostramos el récord!
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
            }
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
                    text = """
                        ¡Estas Muerto!
                        Puntos Finales: ${state.score}
                        Récord Máximo: ${state.highScore}
                    """.trimIndent(),
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}