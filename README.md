# OpenArcade

OpenArcade is a sophisticated, arcade-inspired game launcher meticulously crafted for Android. It
serves as a centralized command center for your entire mobile gaming collection, blending a refined
aesthetic with powerful customization and deep play-time insights.

## Core Features

- **Intelligent Game Discovery**: Seamlessly identifies and aggregates all installed titles into a
  unified, high-performance library.
- **Granular Categorization**: Organize your collection with precision using bespoke categories
  tailored to your gaming habits.
- **Advanced Play-time Analytics**: Gain profound insights into your gaming patterns with
  comprehensive, data-driven usage statistics.
- **Bespoke Personalization**: Complete creative control over your library, allowing you to redefine
  game titles and curate custom iconography.
- **Immersive Dynamic Theming**: An adaptive interface that extracts the essence of your game
  artwork to create a cohesive, color-aware visual experience.
- **High-Fidelity Navigation**: Experience fluid, cinematic transitions as you move between the
  library, deep-dive detail views, and system settings.

## Technical Architecture

OpenArcade leverages a cutting-edge technical stack to ensure performance, stability, and
maintainability:

- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Powers a fully declarative,
  reactive UI layer for maximum responsiveness.
- **[Koin](https://insert-koin.io/)** - Facilitates a lightweight and efficient dependency injection
  framework.
- **[Room](https://developer.android.com/training/data-storage/room)** - Provides a robust,
  ACID-compliant local database for managing extensive game data and analytics.
- **[Voyager](https://voyager.adriel.cafe/)** - Implements a pragmatic and flexible navigation
  architecture specifically designed for Compose.
- **[Coil](https://coil-kt.github.io/coil/)** - Ensures high-performance, lifecycle-aware image
  loading and caching.
- **[Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)** - Drives
  asynchronous operations and reactive data streams for a non-blocking user experience.
- **[Palette API](https://developer.android.com/develop/ui/views/graphics/palette)** - Delivers a
  modern design language with sophisticated color extraction for dynamic interface adaptation.

## Project Structure

```text
app/src/main/java/off/kys/openarcade/
├── analytics/    # Comprehensive tracking and statistical engines
├── data/         # Data persistence layer and repository implementations
├── di/           # Dependency injection modules and configuration
├── domain/       # Pure business logic, entity models, and use cases
├── service/      # Essential background services and lifecycle management
├── ui/           # Multi-layered Jetpack Compose presentation layer
│   ├── detail/   # Immersive game information and action views
│   ├── launcher/ # Primary interactive library interface
│   ├── main/     # Application entry point and navigation orchestration
│   └── theme/    # Sophisticated Material 3 design system implementation
└── util/         # Foundational utility functions and extensions
```

## Getting Started

### Prerequisites

- Android Studio Ladybug (or newer)
- Android SDK 37 (Target)
- Minimum Android Version: Android 6.0 (API 23)

### Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/kys0ff/OpenArcade.git
   ```
2. Open the project in Android Studio.
3. Synchronize the project with Gradle files.
4. Deploy the `app` module to a target device or emulator.

## Releases

- **[1.0.0](https://github.com/kys0ff/OpenArcade/releases/tag/v1.0.0)** - The inaugural release
  featuring core library management, automated play-time tracking, and adaptive dynamic theming.

## License

This project is licensed under the MIT License. Detailed terms can be found in
the [LICENSE](LICENSE) file.
