package com.campos.atrapamichis.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campos.atrapamichis.domain.model.Cat
import com.campos.atrapamichis.domain.model.Position
import com.campos.atrapamichis.domain.usecase.CheckCollisionUseCase
import com.campos.atrapamichis.domain.usecase.MoveCatUseCase
import com.campos.atrapamichis.domain.usecase.SpawnItemUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    // Instanciar caso de uso
    private val moveCateUseCase = MoveCatUseCase()
    // Instanciar el spawner
    private val spawnItemUseCase = SpawnItemUseCase()
    // Instanciar Colisiones
    private val checkCollisionUseCase = CheckCollisionUseCase()

    // Controles de movimiento
    private var isMovingLeft = false
    private var isMovingRight = false

    // Un contador para saber cuando generar un objeto
    private var framesSinceLastSpawn = 0

    init {
        startGameLoop()
    }

    // Motor del juego
    private fun startGameLoop() {
        // viewModelScope es una corrutina que vive exactamente lo que viva esta pantalla
        viewModelScope.launch {
            while (true) {
                if (!_state.value.isGameOver) {
                    updateGameLogic()
                }
                // 16 milisegundos de espera = ~60 Frames Por Segundo (1000ms / 60)
                delay(16L.milliseconds)
            }
        }
    }

    private fun updateGameLogic() {
        val currentState = _state.value

        // Evitamos calcular si la pantalla aún no nos dice cuanto mide
        if (currentState.screenWidth == 0f) return

        // 1. Movemos al gato usando nuestro Caso de Uso pur
        val updateCat = moveCateUseCase(
            cat = currentState.cat,
            isMovingLeft = isMovingLeft,
            isMovingRight = isMovingRight,
            screenWidth = currentState.screenWidth
        )

        // 2. Hacermos caer los objetos
        val movedItems = currentState.fallingItem.map { item ->
            item.copy(position = Position(item.position.x, item.position.y + item.fallSpeed))
        }.filter {
            // Eliminamos los que ya salieron de la pantalla por abajo para liberar memoria
            it.position.y < currentState.screenHeight + 100f
        }

        // 3. Revisar Colisiones
        val collitionResult = checkCollisionUseCase(updateCat, movedItems)

        // 4. Calcular nuevos puntos y vidas
        val newScore = currentState.score + collitionResult.addScore
        val newLives = currentState.lives - collitionResult.lostLives
        val gameOver = newLives <= 0

        // 5. Spawner de nuevos items
        val finalItems = collitionResult.remainingItems.toMutableList()
        framesSinceLastSpawn++
        if (framesSinceLastSpawn >= 60){
            val newItem = spawnItemUseCase(currentState.screenWidth)
            finalItems.add(newItem)
            framesSinceLastSpawn = 0
        }

        // 2. Emitimos el nuevo estado (Compose redibujara la pantalla)
        _state.value = currentState.copy(
            cat = updateCat,
            fallingItem = movedItems
        )
    }


    // Funcinces que la UI llamará cuando el jugador toque la pantalla
    fun updateScreenDimensions(width: Float, height: Float) {
        if (_state.value.screenWidth == 0f) {
            // Inicializamos al gato en el centro inferior de la pantalla
            val initialCat = Cat(position = Position(x = width / 2f, y = height - 150f))
            _state.value = _state.value.copy(
                screenWidth = width,
                screenHeight = height,
                cat = initialCat
            )
        }
    }

    fun moveLeft(active: Boolean) { isMovingLeft = active }
    fun moveRight(active: Boolean) { isMovingRight = active }
}
