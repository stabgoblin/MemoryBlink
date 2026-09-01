package com.example.memoryblink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.memoryblink.data.HighScoreRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.SecureRandom

enum class Difficulty { Easy, Medium, Hard }

enum class GameStatus {
    Idle, Playing, PlayerTurn, GameOver, RoundCleared
}

data class GameState(
    val status: GameStatus = GameStatus.Idle,
    val sequence: List<Int> = emptyList(),
    val currentStep: Int = 0,
    val round: Int = 0,
    val highScoreEasy: Int = 0,
    val highScoreMedium: Int = 0,
    val highScoreHard: Int = 0,
    val difficulty: Difficulty = Difficulty.Easy,
    val currentlyBlinkingIndex: Int? = null
)

class GameViewModel(private val repository: HighScoreRepository) : ViewModel() {

    private val secureRandom = SecureRandom()

    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getHighScoreFlow(Difficulty.Easy).collect { score ->
                _uiState.update { it.copy(highScoreEasy = score) }
            }
        }
        viewModelScope.launch {
            repository.getHighScoreFlow(Difficulty.Medium).collect { score ->
                _uiState.update { it.copy(highScoreMedium = score) }
            }
        }
        viewModelScope.launch {
            repository.getHighScoreFlow(Difficulty.Hard).collect { score ->
                _uiState.update { it.copy(highScoreHard = score) }
            }
        }
    }

    fun startGame(difficulty: Difficulty) {
        _uiState.update {
            it.copy(
                status = GameStatus.Playing,
                sequence = emptyList(),
                currentStep = 0,
                round = 0,
                currentlyBlinkingIndex = null,
                difficulty = difficulty
            )
        }
        nextRound()
    }

    private fun nextRound() {
        val lastButton = _uiState.value.sequence.lastOrNull()
        val gridSize = when (_uiState.value.difficulty) {
            Difficulty.Easy -> 3
            Difficulty.Medium -> 4
            Difficulty.Hard -> 5
        }
        val gridCount = gridSize * gridSize
        
        var newButton = secureRandom.nextInt(gridCount)
        if (lastButton != null) {
            val lastRow = lastButton / gridSize
            val lastCol = lastButton % gridSize
            val halfway = gridSize / 2.0
            val middleIdx = gridSize / 2
            
            val applyRule1 = secureRandom.nextBoolean()
            val applyRule2 = secureRandom.nextBoolean()
            val applyRule3 = secureRandom.nextBoolean()
            val recentButtons = _uiState.value.sequence.takeLast(3)
            
            var attempts = 0
            while (true) {
                attempts++
                val newRow = newButton / gridSize
                val newCol = newButton % gridSize
                
                var isValid = newButton != lastButton
                
                if (attempts < 50) {
                    if (isValid && applyRule1) {
                        val lastIsMiddle = (gridSize % 2 != 0) && (lastRow == middleIdx || lastCol == middleIdx)
                        val newIsMiddle = (gridSize % 2 != 0) && (newRow == middleIdx || newCol == middleIdx)
                        
                        if (!lastIsMiddle && !newIsMiddle) {
                            val lastHalf = if (lastRow < halfway) 0 else 1
                            val newHalf = if (newRow < halfway) 0 else 1
                            if (newHalf == lastHalf) isValid = false
                        }
                    }
                    
                    if (isValid && applyRule2) {
                        if (newRow == lastRow || newCol == lastCol) {
                            isValid = false
                        }
                    }
                    
                    if (isValid && applyRule3) {
                        if (recentButtons.contains(newButton)) {
                            isValid = false
                        }
                    }
                }
                
                if (isValid) {
                    break
                }
                newButton = secureRandom.nextInt(gridCount)
            }
        }
        val newSequence = _uiState.value.sequence + newButton
        
        _uiState.update {
            it.copy(
                status = GameStatus.Playing,
                sequence = newSequence,
                round = it.round + 1,
                currentStep = 0
            )
        }
        playSequence()
    }

    private fun playSequence() {
        viewModelScope.launch {
            delay(500) // Brief pause before starting playback
            
            val sequenceToPlay = if (_uiState.value.difficulty == Difficulty.Hard) {
                listOf(_uiState.value.sequence.last())
            } else {
                _uiState.value.sequence
            }
            
            val delayTime = if (_uiState.value.difficulty == Difficulty.Medium) {
                (500 - (_uiState.value.round * 40)).coerceAtLeast(150).toLong()
            } else {
                500L
            }
            val gapTime = if (_uiState.value.difficulty == Difficulty.Medium) {
                (delayTime / 2).coerceAtLeast(100L)
            } else {
                250L
            }
            
            for (index in sequenceToPlay) {
                _uiState.update { it.copy(currentlyBlinkingIndex = index) }
                delay(delayTime) // Button lit up
                _uiState.update { it.copy(currentlyBlinkingIndex = null) }
                delay(gapTime) // Gap between blinks
            }
            _uiState.update { it.copy(status = GameStatus.PlayerTurn) }
        }
    }

    fun onButtonTapped(index: Int) {
        val state = _uiState.value
        if (state.status != GameStatus.PlayerTurn) return

        val expectedIndex = state.sequence.getOrNull(state.currentStep)
        if (index == expectedIndex) {
            val nextStep = state.currentStep + 1
            if (nextStep == state.sequence.size) {
                // Round cleared
                _uiState.update { it.copy(status = GameStatus.RoundCleared) }
                viewModelScope.launch {
                    delay(1000) // Show "Round Cleared" for a bit
                    nextRound()
                }
            } else {
                _uiState.update { it.copy(currentStep = nextStep) }
            }
        } else {
            // Game over
            _uiState.update { it.copy(status = GameStatus.GameOver) }
            viewModelScope.launch {
                repository.saveHighScore(state.round - 1, state.difficulty)
            }
        }
    }
    
    fun returnToMenu() {
        _uiState.update {
            it.copy(
                status = GameStatus.Idle,
                sequence = emptyList(),
                currentStep = 0,
                round = 0
            )
        }
    }
}

class GameViewModelFactory(private val repository: HighScoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
