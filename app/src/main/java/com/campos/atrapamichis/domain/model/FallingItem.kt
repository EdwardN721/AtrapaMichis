package com.campos.atrapamichis.domain.model

import com.campos.atrapamichis.domain.enums.ItemType

data class FallingItem(
    val id: String,
    val type: ItemType,
    val position: Position,
    val radius: Float = 40f,
    val fallSpeed: Float = 10f
)
