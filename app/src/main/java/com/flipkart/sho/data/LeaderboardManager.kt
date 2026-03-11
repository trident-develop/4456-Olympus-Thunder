package com.flipkart.sho.data

import android.content.Context
import com.flipkart.sho.game.model.MatchResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class LeaderboardManager(context: Context) {
    private val prefs = context.getSharedPreferences("leaderboard", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveMatch(result: MatchResult) {
        val matches = getMatches().toMutableList()
        matches.add(0, result)
        val json = gson.toJson(matches)
        prefs.edit { putString(KEY_MATCHES, json) }
    }

    fun getMatches(): List<MatchResult> {
        val json = prefs.getString(KEY_MATCHES, null) ?: return emptyList()
        val type = object : TypeToken<List<MatchResult>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        private const val KEY_MATCHES = "match_history"
    }
}
