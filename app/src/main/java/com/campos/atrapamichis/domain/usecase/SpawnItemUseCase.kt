package com.campos.atrapamichis.domain.usecase

import com.campos.atrapamichis.domain.enums.ItemType
import com.campos.atrapamichis.domain.model.FallingItem
import com.campos.atrapamichis.domain.model.Position
import java.util.UUID
import kotlin.random.Random

class SpawnItemUseCase {
    operator fun invoke(screenWidth: Float): FallingItem {
        // Posicion X aleatoria asegurando que no aparezca pegado a las orillas
        val randomX = Random.nextFloat() * (screenWidth - 100f) + 50f

        // Decidir aleatoriamente el tipo de objeto cae
        val type = when (Random.nextInt(100)) {
            in 0..45 -> ItemType.FISH // 46% de probabilidad
            in 46..75 -> ItemType.YARN // 30% de probabilidad
            else -> ItemType.CUCUMBER // 24% de probabilidad
        }

        return FallingItem(
            id = UUID.randomUUID().toString(), // Id unico para Compose
            type = type,
            position = Position(x = randomX, y = -50f), // Inicia fuera de la pantalla
            fallSpeed = Random.nextFloat() * 6f + 9f // Velocidad de caida variable
        )
    }
}
