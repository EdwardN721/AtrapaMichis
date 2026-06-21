package com.campos.atrapamichis.presentation.game

import com.campos.atrapamichis.domain.enums.CatEmotion
import com.campos.atrapamichis.domain.model.Cat
import com.campos.atrapamichis.domain.model.FallingItem
import com.campos.atrapamichis.domain.model.Position

// Representación del estatus del juego
data class GameState (
    val cat: Cat = Cat(position = Position(0f, 0f)),
    val fallingItem: List<FallingItem> = emptyList(),
    val score: Int = 0,
    val lives: Int = 3,
    val isGameOver: Boolean = false,
    val screenWidth: Float = 0f,
    val screenHeight: Float = 0f,
    val highScore: Int = 0,
    val catEmotion: CatEmotion = CatEmotion.NORMAL
)