package com.example.memoryblink.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.memoryblink.viewmodel.Difficulty
import com.example.memoryblink.viewmodel.GameState
import com.example.memoryblink.viewmodel.GameStatus
import com.example.memoryblink.viewmodel.GameViewModel

enum class ScreenType { Start, Game, GameOver }

@Composable
fun MemoryBlinkApp(viewModel: GameViewModel) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val currentScreen = when (state.status) {
            GameStatus.Idle -> ScreenType.Start
            GameStatus.GameOver -> ScreenType.GameOver
            else -> ScreenType.Game
        }

        AnimatedContent(
            targetState = currentScreen,
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                ScreenType.Start -> {
                    StartScreen(
                        highScoreEasy = state.highScoreEasy,
                        highScoreMedium = state.highScoreMedium,
                        highScoreHard = state.highScoreHard,
                        onStartEasy = { viewModel.startGame(Difficulty.Easy) },
                        onStartMedium = { viewModel.startGame(Difficulty.Medium) },
                        onStartHard = { viewModel.startGame(Difficulty.Hard) }
                    )
                }
                ScreenType.GameOver -> {
                    val highScoreForDiff = when (state.difficulty) {
                        Difficulty.Easy -> state.highScoreEasy
                        Difficulty.Medium -> state.highScoreMedium
                        Difficulty.Hard -> state.highScoreHard
                    }
                    GameOverScreen(
                        score = state.round - 1, // Round is incremented when starting, so actual score is round - 1
                        highScore = highScoreForDiff,
                        onPlayAgain = { viewModel.startGame(state.difficulty) },
                        onBackToMenu = { viewModel.returnToMenu() }
                    )
                }
                ScreenType.Game -> {
                    GameScreenContent(
                        state = state,
                        onButtonTapped = { viewModel.onButtonTapped(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun StartScreen(
    highScoreEasy: Int,
    highScoreMedium: Int,
    highScoreHard: Int,
    onStartEasy: () -> Unit,
    onStartMedium: () -> Unit,
    onStartHard: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Memory Blink",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Easy High Score: $highScoreEasy", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
        Text(text = "Medium High Score: $highScoreMedium", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
        Text(text = "Hard High Score: $highScoreHard", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onStartEasy,
            modifier = Modifier.size(width = 200.dp, height = 56.dp)
        ) {
            Text(text = "Easy Mode", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onStartMedium,
            modifier = Modifier.size(width = 200.dp, height = 56.dp)
        ) {
            Text(text = "Medium Mode", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onStartHard,
            modifier = Modifier.size(width = 200.dp, height = 56.dp)
        ) {
            Text(text = "Hard Mode", fontSize = 20.sp)
        }
    }
}

@Composable
fun GameOverScreen(score: Int, highScore: Int, onPlayAgain: () -> Unit, onBackToMenu: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Game Over",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Final Score: $score", fontSize = 24.sp)
        Text(text = "High Score: $highScore", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onPlayAgain,
            modifier = Modifier.size(width = 200.dp, height = 56.dp)
        ) {
            Text(text = "Play Again", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onBackToMenu,
            modifier = Modifier.size(width = 200.dp, height = 56.dp)
        ) {
            Text(text = "Back to Menu", fontSize = 20.sp)
        }
    }
}

@Composable
fun GameScreenContent(state: GameState, onButtonTapped: (Int) -> Unit) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val highScore = when (state.difficulty) {
                Difficulty.Easy -> state.highScoreEasy
                Difficulty.Medium -> state.highScoreMedium
                Difficulty.Hard -> state.highScoreHard
            }
            val diffText = state.difficulty.name
            
            Column {
                Text(text = "Round: ${state.round}", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                Text(text = diffText, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            }
            Text(text = "High Score: $highScore", fontSize = 20.sp)
        }

        // Status Text
        val statusText = when (state.status) {
            GameStatus.Playing -> "Watch the pattern..."
            GameStatus.PlayerTurn -> "Your turn!"
            GameStatus.RoundCleared -> "Round Cleared!"
            else -> ""
        }
        
        Text(
            text = statusText,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Grid
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val gridSize = when (state.difficulty) {
                Difficulty.Easy -> 3
                Difficulty.Medium -> 4
                Difficulty.Hard -> 5
            }
            
            Column(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (row in 0 until gridSize) {
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0 until gridSize) {
                            val index = row * gridSize + col
                            val isBlinking = state.currentlyBlinkingIndex == index
                            val isEnabled = state.status == GameStatus.PlayerTurn

                            GameButton(
                                index = index,
                                baseColor = MaterialTheme.colorScheme.primary,
                                isBlinking = isBlinking,
                                isEnabled = isEnabled,
                                modifier = Modifier.weight(1f).aspectRatio(1f),
                                onClick = {
                                    if (isEnabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onButtonTapped(index)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameButton(
    index: Int,
    baseColor: Color,
    isBlinking: Boolean,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Determine if button should appear highlighted (either blinking by game, or pressed by user)
    val isHighlighted = isBlinking || (isPressed && isEnabled)

    val scale by animateFloatAsState(
        targetValue = if (isHighlighted) 0.9f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "button_scale"
    )

    // Adjust color brightness for highlight effect
    val alpha by animateFloatAsState(
        targetValue = if (isHighlighted) 1f else 0.6f,
        animationSpec = tween(durationMillis = 150),
        label = "button_alpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(baseColor.copy(alpha = alpha))
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Disable default ripple
                enabled = isEnabled,
                onClick = onClick
            )
    )
}
