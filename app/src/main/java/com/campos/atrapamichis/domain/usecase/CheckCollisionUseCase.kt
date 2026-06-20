package com.campos.atrapamichis.domain.usecase

import com.campos.atrapamichis.domain.enums.ItemType
import com.campos.atrapamichis.domain.model.Cat
import com.campos.atrapamichis.domain.model.CollitionResult
import com.campos.atrapamichis.domain.model.FallingItem

class CheckCollisionUseCase {
    operator fun invoke(cat: Cat, items: List<FallingItem>): CollitionResult{
        var addedScore = 0
        var lostLives = 0
        val remainingItems = mutableListOf<FallingItem>()

        // Definimos los bordes del rectangulo del gato (según su centro
        val rectLeft = cat.position.x - (cat.width / 2f)
        val rectRight = cat.position.x + (cat.width / 2f)
        val rectTop = cat.position.y - (cat.height / 2f)
        val rectBottom = cat.position.y - (cat.height / 2f)

        for (item in items){
            // Encontramos el punto del rectangulo más cercano al centro del circulo
            val closestX = item.position.x.coerceIn(rectLeft, rectRight)
            val closestY = item.position.y.coerceIn(rectTop, rectBottom)

            // Calculamos la distancia entre el centro del círculo y ese punto cercano
            val distanceX = item.position.x - closestX
            val distanceY = item.position.y - closestY

            // Teorema de Pitágoras (usamos distancia al cuadrado para ahorrar recursos de CPU al no usar raíz cuadrada)
            val distanceSquared = (distanceX * distanceX) + (distanceY * distanceY)
            val isColliding = distanceSquared < (item.radius * item.radius)

            if (isColliding) {
                // Si choca, aplicamos las reglas de negocio según el tipo de ítem
                when (item.type){
                    ItemType.FISH -> addedScore += 10
                    ItemType.YARN -> addedScore += 15
                    ItemType.CUCUMBER -> lostLives += 1
                }
                // Como colisionó, no lo agregamos a 'remainingItems' (desaparece de la pantalla)
            } else {
                remainingItems.add(item)
            }
        }
        return CollitionResult(remainingItems, addedScore, lostLives)
    }
}