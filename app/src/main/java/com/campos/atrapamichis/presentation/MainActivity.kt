package com.campos.atrapamichis.presentation

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.campos.atrapamichis.presentation.game.GameScreen
import com.campos.atrapamichis.presentation.game.GameViewModel
import com.campos.atrapamichis.ui.theme.AtrapaMichisTheme

class MainActivity : ComponentActivity() {
    // Delegado para crear o recuperar el viewModel
    private val gameViewModel: GameViewModel by viewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent arranca el entorno de Jetpack Compose
        setContent {
            // Llamamos a nuestra pantalla principal pasandole el ViewModel
            GameScreen(viewModel = gameViewModel)
        }
    }
}
