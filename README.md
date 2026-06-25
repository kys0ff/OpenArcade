# OpenArcade

OpenArcade is a modern, arcade-inspired game launcher for Android. It provides a centralized hub for all your mobile games, featuring a clean UI, deep customization, and insightful play-time analytics.

## 🚀 Features

- **Centralized Game Library**: Automatically detects and organizes your installed games.
- **Custom Categorization**: Group games into custom categories for better organization.
- **Play-time Analytics**: Track how much time you spend in each game with detailed statistics.
- **Deep Customization**: Personalize game titles and icons to fit your style.
- **Dynamic Themes**: Beautiful, color-aware UI that adapts to game artwork.
- **Fluid Navigation**: Seamless transitions between launcher, details, and settings.

## 🛠️ Tech Stack

OpenArcade is built using modern Android development practices and libraries:

- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Declarative UI framework.
- **[Koin](https://insert-koin.io/)** - Lightweight dependency injection.
- **[Room](https://developer.android.com/training/data-storage/room)** - Robust local database for game data and analytics.
- **[Voyager](https://voyager.adriel.cafe/)** - Pragmatic navigation library for Compose.
- **[Coil](https://coil-kt.github.io/coil/)** - Image loading library for Android.
- **[Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)** - Asynchronous programming and reactive data streams.
- **[Palette API](https://developer.android.com/develop/ui/views/graphics/palette)** - Color extraction from game icons for dynamic UI elements.

## 📁 Project Structure

```text
app/src/main/java/off/kys/openarcade/
├── analytics/    # Analytics tracking and statistics logic
├── data/         # Data layer (Room DB, Repositories)
├── di/           # Dependency Injection modules (Koin)
├── domain/       # Business logic (Models, Use Cases)
├── service/      # Background services (e.g., play-time tracking)
├── ui/           # UI layer (Compose screens and components)
│   ├── detail/   # Game details screen
│   ├── launcher/ # Main launcher interface
│   ├── main/     # Root activity and navigation
│   └── theme/    # App-wide Material 3 theme
└── util/         # Common utility functions
```

## 🏁 Getting Started

### Prerequisites

- Android Studio Ladybug (or newer)
- Android SDK 37 (Target)
- Minimum Android Version: Android 6.0 (API 23)

### Build and Run

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/OpenArcade.git
   ```
2. Open the project in Android Studio.
3. Sync Project with Gradle Files.
4. Run the `app` module on an emulator or physical device.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details (or add one if applicable).
