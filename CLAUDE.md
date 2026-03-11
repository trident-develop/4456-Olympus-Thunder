# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Olympus Thunder is an Android chess game (package: `com.flipkart.sho`) built with Kotlin and Jetpack Compose. Single-module project.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests
```

## Architecture

- **Min SDK**: 28, **Target/Compile SDK**: 36, **Java**: 11
- **UI**: Jetpack Compose + Material 3, edge-to-edge, portrait only
- **Navigation**: Navigation Compose (NavHost in MainActivity)
- **Data**: SharedPreferences + Gson for settings and match history

### Activities
- `LoadingActivity` — splash screen (2s), then launches MainActivity
- `MainActivity` — hosts NavHost for all screens

### Key Packages
- `navigation/` — Routes + AppNavGraph
- `ui/screens/` — loading, menu, leaderboard, settings, setup, game, howtoplay, privacy
- `ui/components/` — SquareButton, MenuButton, pressableWithCooldown, ChessBackground
- `game/model/` — ChessPiece, GameState, Position, Move, MatchResult
- `game/logic/` — ChessEngine (game state machine), MoveValidator (move validation, check/checkmate/stalemate)
- `data/` — SettingsManager, LeaderboardManager
- `audio/` — MusicManager (singleton, background music)

### Chess Engine
- Board: row 0 = white (top), row 7 = black (bottom)
- White pawns move down (increasing row), black pawns move up
- Full move validation including castling, pawn promotion, check/checkmate/stalemate
- Draw by mutual agreement (both players press Draw button)

### Existing Resources
- Drawables: bg_1, back_button, home_button, replay_button, pause_button, main_button, pop_up_1, loading_progress_bg, title images (*_tittle.png)
- Font: res/font/font.ttf (used as GameFont)
- Audio: res/raw/music_bg.mp3
- Dependencies managed via gradle/libs.versions.toml
