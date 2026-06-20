package com.campos.atrapamichis.domain.model

data class CollitionResult(
    val remainingItems: List<FallingItem>,
    val addScore: Int,
    val lostLives: Int
)
