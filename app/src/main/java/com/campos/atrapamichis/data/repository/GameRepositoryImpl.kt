package com.campos.atrapamichis.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.campos.atrapamichis.domain.repository.GameRespository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Esta extensión nos asegura tener una única instancia de DataStore (Singleton)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_prefs")

class GameRepositoryImpl(private val context: Context) : GameRespository {
    // Definimos la llave con la que guardamos el valor
    private val HIGH_SCORE_KEY = intPreferencesKey("high_score")

    // Retorna un flujo constante de datos. Si el valor cambia en disco, se emitirá automáticamente
    override fun getHighScore(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[HIGH_SCORE_KEY] ?: 0
        }
    }

    // Guarda de forma asíncrona y segura
    override suspend fun saveHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentHighScore = preferences[HIGH_SCORE_KEY] ?: 0
            if (score > currentHighScore) {
                preferences[HIGH_SCORE_KEY] = score
            }
        }
    }

}