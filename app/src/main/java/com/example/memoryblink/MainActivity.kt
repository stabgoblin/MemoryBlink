package com.example.memoryblink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.memoryblink.data.HighScoreRepository
import com.example.memoryblink.ui.MemoryBlinkApp
import com.example.memoryblink.ui.theme.BrainyTheme
import com.example.memoryblink.viewmodel.GameViewModel
import com.example.memoryblink.viewmodel.GameViewModelFactory

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