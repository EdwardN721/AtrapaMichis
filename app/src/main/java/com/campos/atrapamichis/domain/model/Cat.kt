package com.campos.atrapamichis.domain.model

// Personaje principal
data class Cat (
    val position: Position,
    val width: Float = 150f,
    val height: Float = 150f,
    var speed: Float = 30f // Qué tan rápido se mueve de lado a lado
)