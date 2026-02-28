# CLAUDE.md - Notificity Codebase Guide

A comprehensive guide for AI assistants working on the Notificity Android application.

## Project Overview

**Notificity** is an Android application designed to capture, categorize, and search incoming push notifications. It enhances user productivity and notification management on Android devices.

**Repository**: https://github.com/darshanpania/Notificity
**Current Version**: 1.2.0 (versionCode: 6)
**Minimum SDK**: 26
**Target SDK**: 35
**JVM Target**: Java 17

## Technology Stack

### Core Framework & UI
- **Kotlin**: 2.0.21 - Primary language
- **Jetpack Compose**: 2025.05.01 - Modern declarative UI framework
- **Material Design 3** - UI component library
- **AndroidX Core**: 1.16.0

### Database & Persistence
- **Room Database**: 2.6.1 - Type-safe database abstraction
- **DataStore Preferences**: 1.1.7 - Key-value storage
- **Status**: Database is at version 3 with 2 migrations

### Dependency Injection
- **Hilt**: 2.56.2 - Compile-time DI framework (Dagger)

### Firebase Integration
- **Firebase BOM**: 33.14.0
- **Firebase Analytics**: Telemetry and crash reporting
- **Firebase Crashlytics**: Error tracking
- **Firebase Cloud Messaging**: Push notifications

### Build & Code Quality
- **Gradle**: 8.6.1 - Build system
- **KSP**: 2.0.21-1.0.27 - Kotlin Symbol Processing for code generation
- **Spotless**: 6.25.0 - Code formatting (ktfmt with Dropbox style)
- **Google Services Plugin**: 4.4.2 - Firebase integration

### Additional Libraries
- **Glide**: 4.16.0 - Image loading and caching
- **Lottie**: 6.1.0 - Animation library
- **JUnit**: 4.13.2 - Testing framework
- **Espresso**: 3.6.1 - UI testing

## Project Structure

```
Notificity/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/com/darshan/notificity/
│   │   │       ├── MainActivity.kt              # Main entry point (Jetpack Compose)
│   │   │       ├── NotificityListener.kt        # NotificationListenerService implementation
│   │   │       ├── NotificationsActivity.kt     # Notifications list activity
│   │   │       ├── AboutActivity.kt             # About screen
│   │   │       ├── SettingsActivity.kt          # Settings UI
│   │   │       ├── AppDatabase.kt               # Room database definition
│   │   │       ├── NotificationEntity.kt        # Database entity
│   │   │       ├── NotificationDao.kt           # Data access object
│   │   │       ├── MyApplication.kt             # Application class with Hilt setup
│   │   │       ├── Constants.kt                 # App-wide constants
│   │   │       ├── Color.kt                     # Color definitions
│   │   │       ├── Converters.kt                # Room type converters
│   │   │       ├── AppInfo.kt                   # App metadata holder
│   │   │       │
│   │   │       ├── main/
│   │   │       │   ├── ui/
│   │   │       │   │   └── MainActivity.kt      # Main activity with Compose UI
│   │   │       │   ├── viewmodel/
│   │   │       │   │   └── MainViewModel.kt     # Business logic and state management
│   │   │       │   └── data/
│   │   │       │       ├── NotificationRepository.kt    # Repository interface
│   │   │       │       └── NotificationRepositoryImpl.kt # Repository implementation
│   │   │       │
│   │   │       ├── ui/
│   │   │       │   ├── theme/
│   │   │       │   │   ├── NotificityTheme.kt          # Material Design 3 theme
│   │   │       │   │   ├── ThemeMode.kt                # Dark/Light theme mode enum
│   │   │       │   │   └── ThemePreferenceManager.kt   # Theme persistence
│   │   │       │   ├── settings/
│   │   │       │   │   ├── SettingsActivity.kt
│   │   │       │   │   └── SettingsViewModel.kt
│   │   │       │   └── BaseActivity.kt          # Base activity class
│   │   │       │
│   │   │       ├── di/
│   │   │       │   └── AppModule.kt             # Hilt dependency injection module
│   │   │       │
│   │   │       ├── analytics/
│   │   │       │   ├── AnalyticsEvent.kt        # Event data model
│   │   │       │   ├── AnalyticsConstants.kt    # Event names/parameters
│   │   │       │   ├── AnalyticsService.kt      # Analytics interface
│   │   │       │   ├── AnalyticsLogger.kt       # Logging interface
│   │   │       │   ├── AnalyticsTracker.kt      # Main tracker
│   │   │       │   └── FirebaseAnalyticsTracker.kt # Firebase implementation
│   │   │       │
│   │   │       ├── components/
│   │   │       │   ├── NotificityAppBar.kt      # Reusable app bar
│   │   │       │   ├── EmptyContentState.kt     # Empty state UI
│   │   │       │   ├── SwipeToDelete.kt         # Swipe gesture handler
│   │   │       │   ├── BuyMeACoffee.kt          # Support button
│   │   │       │   └── ClickableSection.kt      # Clickable section component
│   │   │       │
│   │   │       ├── fcm/
│   │   │       │   ├── NotificationContent.kt           # FCM message model
│   │   │       │   ├── AppFirebaseMessagingService.kt   # FCM listener
│   │   │       │   └── NotificationHandler.kt           # Message handling
│   │   │       │
│   │   │       ├── enums/
│   │   │       │   └── NotificationPermissionStatus.kt   # Permission status enum
│   │   │       │
│   │   │       ├── extensions/
│   │   │       │   └── Extensions.kt             # Utility extension functions
│   │   │       │
│   │   │       ├── utils/
│   │   │       │   ├── Logger.kt                 # Logging utility
│   │   │       │   ├── NotificationUtil.kt       # Notification utilities
│   │   │       │   ├── PreferenceManager.kt      # SharedPreferences wrapper
│   │   │       │   └── Util.kt                   # General utilities
│   │   │       │
│   │   │       └── validation/
│   │   │           └── NotificationValidator.kt  # Notification filtering logic
│   │   │
│   │   ├── test/
│   │   │   └── java/com/darshan/notificity/
│   │   │       └── ExampleUnitTest.kt
│   │   │
│   │   └── androidTest/
│   │       └── java/com/darshan/notificity/
│   │           └── ExampleInstrumentedTest.kt
│   │
│   └── build.gradle.kts                 # App-level build configuration
│
├── gradle/
│   └── libs.versions.toml                # Version catalog (dependency definitions)
│
├── build.gradle.kts                      # Project-level build configuration (Spotless setup)
├── gradle.properties
├── settings.gradle.kts
├── gradlew / gradlew.bat                # Gradle wrapper
│
├── .scripts/
│   └── pre-commit                        # Pre-commit hook for code style
│
├── README.md                             # User-facing documentation
├── CHANGELOG.md                          # Version history
├── CONTRIBUTING.md                       # Contribution guidelines
├── ROADMAP.md                            # Feature roadmap
├── LICENSE                               # MIT License
└── PRIVACY.md                            # Privacy policy
```

## Architecture & Design Patterns

### Overall Architecture: Clean Architecture + MVVM

The codebase follows clean architecture principles with clear separation of concerns:

```
Data Layer (Database, Network)
    ↓
Repository Pattern
    ↓
ViewModel (Business Logic)
    ↓
UI Layer (Jetpack Compose)
```

### Key Patterns

**1. Repository Pattern**
- `NotificationRepository` (interface) - Defines data access contracts
- `NotificationRepositoryImpl` - Concrete implementation using Room
- Located in `main/data/` package
- Provides `getAllNotification()`, `deleteNotification()`, etc.

**2. ViewModel Pattern**
- `MainViewModel` extends `ViewModel`
- Injected with repository via Hilt
- Manages UI state using `StateFlow` and `MutableStateFlow`
- Handles coroutines with `viewModelScope`
- Located in `main/viewmodel/` package

**3. Dependency Injection (Hilt)**
- `AppModule.kt` defines all application-level singletons
- Database, DAOs, PreferenceManager, ThemePreferenceManager
- Migration array configuration for Room
- All modules use `@InstallIn(SingletonComponent::class)`

**4. Room Database**
- Version 3 (managed via `gradle/libs.versions.toml`)
- Automatic migration handling with fallback to destructive migration
- Multi-instance invalidation enabled
- Journal mode: AUTOMATIC
- Migrations tracked in `AppDatabase` companion object

**5. Theme Management**
- Material Design 3 with `NotificityTheme`
- `ThemeMode` enum (LIGHT, DARK, SYSTEM)
- `ThemePreferenceManager` persists choice via DataStore
- Dynamic theme switching supported

## Core Features & Implementation

### 1. Notification Capture
**File**: `NotificityListener.kt`
- Extends `NotificationListenerService`
- Injected with `@AndroidEntryPoint` for Hilt support
- Implements `onNotificationPosted()` for real-time capture
- Validates notifications via `NotificationValidator.isGroupSummary()`
- Extracts: title, content, package name, timestamp, image
- Stores to database asynchronously using coroutines

**Permissions Required**:
```xml
<uses-permission android:name="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" />
```

User must manually grant: Settings → Apps & Notifications → Special App Access → Notification access

### 2. Database Management
**Main Entity**: `NotificationEntity`
- Primary Key: Auto-incrementing `id`
- Fields: `notificationId`, `packageName`, `timestamp`, `appName`, `title`, `content`, `imageUrl`, `extras`
- Table name: `notification`

**Access**: `NotificationDao.kt`
- `getAllNotifications()` - Returns Flow for reactive updates
- `deleteNotification()` - Remove single notification
- `clearAllNotifications()` - Bulk delete

**Migrations**:
- **v1→v2**: Changed primary key to composite (id + packageName)
- **v2→v3**: Changed to auto-increment id, added `notificationId` column
- Both migrations track data integrity

### 3. UI Screens
**MainActivity** (Main Entry Point):
- Jetpack Compose-based
- Uses `MainViewModel` for state
- Displays notifications in grid/list view
- Shows app categorization
- Implements swipe-to-delete gesture
- Handles notification permission dialogs

**SettingsActivity**:
- Theme selection (Light/Dark/System)
- Additional app settings
- Redirects to system notification access settings

**AboutActivity**:
- App information
- Contributors list
- Support/donation links

### 4. Analytics & Crash Reporting
**Package**: `analytics/`
- `AnalyticsTracker` - Main tracking interface
- `FirebaseAnalyticsTracker` - Firebase implementation
- `AnalyticsEvent` - Event data model
- Events logged for: App opens, notification views, settings changes
- Crashes automatically tracked via Firebase Crashlytics

### 5. Firebase Cloud Messaging
**Package**: `fcm/`
- `AppFirebaseMessagingService` - Extends `FirebaseMessagingService`
- Handles remote configuration push notifications
- `NotificationHandler` processes message content
- Integrates with system notification display

## Development Workflows

### Building the Project

```bash
# Sync with Gradle and resolve dependencies
./gradlew sync

# Build debug APK
./gradlew assembleDebug

# Build release APK (unsigned)
./gradlew assembleRelease

# Run on connected device/emulator
./gradlew installDebug

# Run tests
./gradlew test                          # Unit tests
./gradlew connectedAndroidTest          # Instrumented tests

# Check code style
./gradlew spotlessCheck

# Apply code style automatically
./gradlew spotlessApply
```

### Running Tests

**Unit Tests** (`src/test/`):
```bash
./gradlew test
```

**Instrumented Tests** (`src/androidTest/`):
```bash
./gradlew connectedAndroidTest
```

**Test Infrastructure**:
- JUnit 4
- Espresso for UI testing
- Compose testing libraries available

### Code Style & Formatting

**Tools**: Spotless with ktfmt (Dropbox Style)

**Configuration**: `build.gradle.kts` (project root)
- Auto-formats Kotlin files (*.kt)
- Formats Gradle Kotlin scripts (*.kts)
- Formats XML files
- Excludes build directories

**Commands**:
```bash
# Check style violations
./gradlew spotlessCheck

# Auto-fix violations
./gradlew spotlessApply
```

**Pre-commit Hook** (Optional):
```bash
# Set up to run spotlessApply before commits
cp ./.scripts/pre-commit ./.git/hooks
```

**Style Rules**:
- Dropbox ktfmt style (more opinionated than standard Kotlin style)
- Trailing whitespace removal
- Newline at end of files
- 4-space indentation (enforced by ktfmt)

### Git Workflow

**Branch Strategy**:
- Feature branches: `feature/description`
- Bug fixes: `fix/description`
- Development: `develop`
- Main: `main` (stable)

**Commit Guidelines**:
- Write descriptive commit messages
- Reference issues when applicable
- Keep commits focused on single changes

**Pull Request Process**:
- All submissions require code review
- Ensure tests pass before merging
- Spotless formatting must pass
- Update changelog if applicable

### Database Migrations

When modifying `NotificationEntity`:

1. Update the entity class in `NotificationEntity.kt`
2. Increment `@Database(version = N)` in `AppDatabase.kt`
3. Create migration object:
```kotlin
internal val MIGRATION_N_N1: Migration = object : Migration(N, N+1) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Execute SQL migration
    }
}
```
4. Register migration in `AppModule.kt` `provideMigrations()` function
5. Test with real device/emulator

## Key Conventions & Best Practices

### Package Organization

**By Feature**:
- `main/` - Core app functionality
- `ui/` - UI components and activities
- `analytics/` - Analytics integration
- `fcm/` - Firebase Cloud Messaging
- `di/` - Dependency injection

**By Layer**:
- `data/` - Repository and database access
- `viewmodel/` - ViewModels
- `ui/` - UI screens and theme
- `components/` - Reusable Compose components
- `utils/` - Utility functions
- `validation/` - Business logic validation

### Naming Conventions

**Files**:
- Activities: `*Activity.kt` (e.g., `MainActivity.kt`)
- ViewModels: `*ViewModel.kt` (e.g., `MainViewModel.kt`)
- Services: `*Service.kt` (e.g., `AnalyticsService.kt`)
- Entities: `*Entity.kt` (e.g., `NotificationEntity.kt`)
- DAOs: `*Dao.kt` (e.g., `NotificationDao.kt`)

**Classes & Functions**:
- Use PascalCase for classes and interfaces
- Use camelCase for functions and variables
- Private members: prefix with underscore if state-holding (e.g., `_uiState`)
- Public exposed state: no prefix (e.g., `uiState`)

**Composables**:
- Name as PascalCase functions (e.g., `NotificationItem()`)
- Parameters: lambdas last (Kotlin convention)
- State hoisting: lift state to parent

### State Management

**StateFlow Pattern**:
```kotlin
private val _state = MutableStateFlow(initialValue)
val state: StateFlow<State> = _state.asStateFlow()

fun updateState(newValue: State) {
    _state.update { newValue }
}
```

**Coroutines**:
- Use `viewModelScope` in ViewModels
- Use `Dispatchers.IO` for database operations
- Use `Dispatchers.Main` for UI updates (default in viewModelScope)

### Hilt Dependency Injection

**Providing Dependencies**:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMyService(): MyService = MyServiceImpl()
}
```

**Injecting**:
```kotlin
@AndroidEntryPoint
class MyActivity : AppCompatActivity() {
    @Inject lateinit var myService: MyService
}
```

**Constructor Injection** (Preferred for ViewModels):
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    val repository: MyRepository
) : ViewModel()
```

### Room Database Patterns

**Query with Flow** (Reactive):
```kotlin
@Query("SELECT * FROM notification")
fun getAllNotifications(): Flow<List<NotificationEntity>>
```

**Suspension Function** (One-shot):
```kotlin
@Query("DELETE FROM notification WHERE id = :id")
suspend fun deleteNotification(id: Int)
```

**Using in Repository**:
```kotlin
override fun getAllNotifications(): Flow<List<NotificationEntity>> {
    return notificationDao.getAllNotifications()
}
```

**Using in ViewModel**:
```kotlin
viewModelScope.launch(Dispatchers.IO) {
    repository.deleteNotification(entity)
}
```

### Testing Conventions

**Unit Test Location**: `src/test/java/`
**Instrumented Test Location**: `src/androidTest/java/`

**Test Naming**:
- `testFunctionName_WhenCondition_ThenExpectation()`
- Example: `testDeleteNotification_WhenValidId_ThenRemovesFromDatabase()`

### Security Considerations

1. **Sensitive Data**: Avoid logging notification content in production
2. **Database**: Room prevents SQL injection automatically
3. **Firebase**: Ensure Crashlytics data anonymization settings are correct
4. **Permissions**: Always check before accessing notifications
5. **User Data**: Respect PRIVACY.md guidelines

## Troubleshooting & Common Issues

### Build Issues

**"Hilt: Unable to find @HiltViewModel"**
- Ensure class has `@HiltViewModel` annotation
- Check it extends `ViewModel`
- Verify it's in Hilt-enabled activity with `@AndroidEntryPoint`

**"Room can't find migration"**
- Check migration class is returned from `provideMigrations()`
- Verify version numbers are sequential
- Ensure migration is added to `AppModule.kt`

**"Spotless code style violations"**
- Run `./gradlew spotlessApply` to auto-fix
- Check ktfmt documentation for specific rules

### Runtime Issues

**Notifications not being captured**
- User hasn't granted notification access permission
- Check `NotificityListener.kt` is registered in manifest
- Verify `NotificationValidator.isGroupSummary()` isn't filtering too aggressively

**Database queries not updating UI**
- Ensure DAO returns `Flow<>` not `suspend fun`
- Verify ViewModel collects flow in `viewModelScope`
- Check `StateFlow` is properly exposed

**Theme not persisting**
- Verify `ThemePreferenceManager` is being used
- Check DataStore permission in manifest
- Ensure theme is applied at app startup

## Contributing Guidelines

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

**Quick Start**:
1. Clone the repository
2. Create feature branch
3. Follow code style (run `spotlessApply`)
4. Write/update tests
5. Commit with clear message
6. Push and create pull request
7. Address code review feedback

**Important**:
- All submissions require review
- Tests must pass
- Code formatting must be correct
- Update changelog if user-facing

## Useful Resources

- **Firebase Documentation**: https://firebase.google.com/docs
- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Hilt Documentation**: https://dagger.dev/hilt
- **Room Database**: https://developer.android.com/training/data-storage/room
- **Kotlin Coroutines**: https://kotlinlang.org/docs/coroutines-overview.html
- **Material Design 3**: https://m3.material.io/

## Recent Changes & Version History

See [CHANGELOG.md](CHANGELOG.md) for complete version history.

**Current Version**: 1.2.0
- Database schema at v3
- Jetpack Compose fully adopted
- Firebase integration complete
- Analytics and crash reporting enabled

## Contact & Support

**Main Repository**: https://github.com/darshanpania/Notificity
**Email**: darshanpania.dev@gmail.com
**Issues**: https://github.com/darshanpania/Notificity/issues

---

**Last Updated**: February 2026
**Status**: This guide covers the current state of the codebase. Update this document when significant architectural changes occur.
