package com.campos.atrapamichis.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.campos.atrapamichis.data.repository.GameRepositoryImpl
import com.campos.atrapamichis.presentation.game.GameScreen
import com.campos.atrapamichis.presentation.game.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Iniciamos el repositorio pasandole el contexto de la aplicacion
        val repository = GameRepositoryImpl(applicationContext)

        // Creamos una fábrica manual para nuestro ViewModel
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST") // Le decimos al compilador que confíe en nuestra validación
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // Validamos que el tipo solicitado sea compatible con GameViewModel
                if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                    return GameViewModel(repository) as T
                }
                throw IllegalArgumentException("Clase ViewModel desconocida")
            }
        }

        val gameViewModel = ViewModelProvider(this, factory)[GameViewModel::class.java]

        // setContent arranca el entorno de Jetpack Compose
        setContent {
            // Llamamos a nuestra pantalla principal pasandole el ViewModel
            GameScreen(viewModel = gameViewModel)
        }
    }
}
