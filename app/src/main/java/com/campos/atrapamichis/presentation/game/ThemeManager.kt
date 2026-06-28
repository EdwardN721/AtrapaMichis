package com.campos.atrapamichis.presentation.game

import com.campos.atrapamichis.domain.enums.GameTheme
import java.time.LocalDate

object ThemeManager {
    fun getCurrentTheme(): GameTheme {
        val today = LocalDate.now()
        val month = today.monthValue
        val day = today.dayOfMonth

        return when {
            // Cumpleaños
            month == 9 && day >= 13 -> GameTheme.BIRTHDAY

            // Día de Muertos: 15 de Octubre al 15 de noviembre
            (month == 10 && day >= 15) || (month == 11 && day <= 15) -> GameTheme.DAY_OF_THE_DEAD

            // Navidad: 15 de Diciembre al 10 de enero
            (month == 12 && day >= 15) || (month == 1 && day <= 10) -> GameTheme.BIRTHDAY

            else -> GameTheme.NORMAL
        }
    }
}