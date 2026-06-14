package com.campos.atrapamichis.domain.usecase

import com.campos.atrapamichis.domain.model.Cat
import com.campos.atrapamichis.domain.model.Position

class MoveCatUseCase {
    operator fun invoke (
        cat: Cat,
        isMovingLeft: Boolean,
        isMovingRight: Boolean,
        screenWidth: Float
        ): Cat {
        var newX = cat.position.x;

        // Velocidad según dirección
        if (isMovingLeft) newX -= cat.speed;
        if (isMovingRight) newX += cat.speed;

        // Colicionar con los bordes
        var halfWidth = cat.width / 2f;

        // Chocar con la pared izquierda
        if (newX < 0f) {
            newX = halfWidth;
        }

        // Chocar con la pared izquierda
        if (newX + halfWidth > screenWidth){
            newX = screenWidth - halfWidth
        }

        return cat.copy(position = Position(newX, cat.position.y));
    }
}