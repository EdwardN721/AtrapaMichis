package com.campos.atrapamichis.presentation.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campos.atrapamichis.R
import com.campos.atrapamichis.domain.enums.CatEmotion
import com.campos.atrapamichis.domain.enums.GameTheme
import com.campos.atrapamichis.domain.enums.ItemType

@Composable
fun GameScreen(viewModel: GameViewModel) {
    // Observamos el estado del ViewModel (equivalente a un Hook o Signal)
    val state by viewModel.state.collectAsState()

    // Obtenemos el tema actual
    val theme = state.currentTheme

    // Decidir fondo
    val backgroundResId = when (theme){
        GameTheme.BIRTHDAY -> R.drawable.birthday_fondo
        GameTheme.DAY_OF_THE_DEAD -> R.drawable.dayofthedead_fondo
        GameTheme.CHRISTMAS -> R.drawable.christmas_fondo
        else -> 0
    }

    // Obtener Id de imagen
    val fishImageRes = obtenerImagenPez(theme)
    val catImageRes = obtenerImagenGato(theme, state.catEmotion)
    val yarnImageRes = obtenerImagenEstambre(theme)
    val cucumberImageRes = obtenerImagenPepinillo(theme)

    // 1. Cargar imágenes en memoria
    val catImage = ImageBitmap.imageResource(id = catImageRes)
    val fishImage = ImageBitmap.imageResource(id = fishImageRes)
    val yarnImage = ImageBitmap.imageResource(id = yarnImageRes)
    val cucumberImage = ImageBitmap.imageResource(id = cucumberImageRes)

    // Box nos permite apilar elementos en el eje Z (uno sobre otro)
    Box(modifier = Modifier.fillMaxSize()) {
        if (backgroundResId != 0){
            Image(
                painter = painterResource(id = backgroundResId),
                contentDescription = "Fondo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF87CEEB)))
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
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

            // 1. Dibujar al gato con la imagen cargada
            drawImage(
                image = catImage,
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

// #region Metodos Privados

private fun obtenerImagenPez(theme: GameTheme): Int {
    return when (theme) {
        GameTheme.BIRTHDAY -> R.drawable.birthday_pastel
        GameTheme.DAY_OF_THE_DEAD -> R.drawable.dayofthedead_pan
        GameTheme.CHRISTMAS -> R.drawable.christmas_calcetin
        else -> R.drawable.normal_fish
    }
}
private fun obtenerImagenGato(theme: GameTheme, emotion: CatEmotion): Int {
    return when (theme){
        GameTheme.BIRTHDAY -> when (emotion){
            CatEmotion.HAPPY -> R.drawable.birthday_cat_happy
            CatEmotion.ANGRY -> R.drawable.birthday_cat_angry
            else -> R.drawable.birthday_cat_normal
        }
        GameTheme.DAY_OF_THE_DEAD -> when (emotion){
            CatEmotion.HAPPY -> R.drawable.dayofthedead_cat_happy
            CatEmotion.ANGRY -> R.drawable.dayofthedead_cat_angry
            else -> R.drawable.dayofthedead_cat_normal
        }
        GameTheme.CHRISTMAS -> when (emotion){
            CatEmotion.HAPPY -> R.drawable.christmas_cat_happy
            CatEmotion.ANGRY -> R.drawable.christmas_cat_angry
            else -> R.drawable.christmas_cat_normal
        }
        else -> when (emotion){
            CatEmotion.HAPPY -> R.drawable.normal_cat_happy
            CatEmotion.ANGRY -> R.drawable.normal_cat_angry
            else -> R.drawable.normal_cat
        }
    }
}

private fun obtenerImagenEstambre(theme: GameTheme): Int {
    return when (theme) {
        GameTheme.BIRTHDAY -> R.drawable.birthday_yarn
        GameTheme.DAY_OF_THE_DEAD -> R.drawable.dayofthedead_yarn
        GameTheme.CHRISTMAS -> R.drawable.christmas_yarn
        else -> R.drawable.normal_yarn
    }
}

private fun obtenerImagenPepinillo(theme: GameTheme): Int {
    return when (theme) {
        GameTheme.BIRTHDAY -> R.drawable.birthday_cucumber
        GameTheme.DAY_OF_THE_DEAD -> R.drawable.dayofthedead_cucumber
        GameTheme.CHRISTMAS -> R.drawable.christmas_cucumber
        else -> R.drawable.normal_cucumber
    }
}

// #endregion

