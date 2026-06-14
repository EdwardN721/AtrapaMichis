package com.campos.atrapamichis.domain.repository

import kotlinx.coroutines.flow.Flow

// Contrato puro para la persistencia de datos
interface GameRespository {
    // Flow es el estándar reactivo de Kotlin,
    fun getHighScore(): Flow<Int>

    // Función suspendida para que se ejecute de forma asíncrona sin bloquear el juego
    suspend fun saveHighScore(score: Int)
}