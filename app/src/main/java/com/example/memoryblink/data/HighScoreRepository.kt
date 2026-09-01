package com.example.memoryblink.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.memoryblink.viewmodel.Difficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_prefs")

class HighScoreRepository(private val context: Context) {

    private val HIGH_SCORE_EASY_KEY = intPreferencesKey("high_score_easy")
    private val HIGH_SCORE_MEDIUM_KEY = intPreferencesKey("high_score_medium")
    private val HIGH_SCORE_HARD_KEY = intPreferencesKey("high_score_hard")

    private fun getKey(difficulty: Difficulty): Preferences.Key<Int> {
        return when (difficulty) {
            Difficulty.Easy -> HIGH_SCORE_EASY_KEY
            Difficulty.Medium -> HIGH_SCORE_MEDIUM_KEY
            Difficulty.Hard -> HIGH_SCORE_HARD_KEY
        }
    }

    fun getHighScoreFlow(difficulty: Difficulty): Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[getKey(difficulty)] ?: 0
        }

    suspend fun saveHighScore(score: Int, difficulty: Difficulty) {
        context.dataStore.edit { preferences ->
            val key = getKey(difficulty)
            val currentScore = preferences[key] ?: 0
            if (score > currentScore) {
                preferences[key] = score
            }
        }
    }
}

