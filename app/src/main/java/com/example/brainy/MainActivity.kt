package com.example.brainy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.brainy.data.HighScoreRepository
import com.example.brainy.ui.MemoryBlinkApp
import com.example.brainy.ui.theme.BrainyTheme
import com.example.brainy.viewmodel.GameViewModel
import com.example.brainy.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val repository = HighScoreRepository(applicationContext)
        val viewModel: GameViewModel by viewModels {
            GameViewModelFactory(repository)
        }
        
        setContent {
            BrainyTheme {
                MemoryBlinkApp(viewModel = viewModel)
            }
        }
    }
}