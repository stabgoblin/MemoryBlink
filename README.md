# Brainy - Memory Blink Game

Brainy is a modern, challenging "Simon Says" style memory game built for Android using Kotlin and Jetpack Compose. Test your memory skills across three different difficulty modes!

## Features

- **Jetpack Compose UI**: Built entirely with Android's modern declarative UI toolkit for smooth and responsive animations.
- **Three Difficulty Modes**:
  - **Easy (3x3 Grid)**: A relaxed mode where the entire pattern is shown every round.
  - **Medium (4x4 Grid)**: A larger grid where the speed of the pattern flashes increases as you advance through rounds.
  - **Hard (5x5 Grid)**: The ultimate challenge! The grid is huge, and only the newest button added to the sequence is shown each round. You must remember the entire sequence from the very beginning.
- **High Scores**: Persistent high score tracking for each difficulty level using Android's DataStore.
- **Haptic Feedback**: Enjoy tactile responses for an immersive gameplay experience.

## Getting Started

### Prerequisites

- Android Studio (latest version recommended)
- Android SDK
- Gradle

### Installation

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/brainy.git
   ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Run the app on an emulator or a physical device.

## Technologies Used

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Data Persistence**: [Preferences DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- **Coroutines & Flow**: For asynchronous programming and state management.

## Contributing

Contributions are welcome! If you'd like to improve the game, add new features, or fix bugs:

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
