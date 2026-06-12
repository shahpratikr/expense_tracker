# Expense Tracker

An offline-first personal finance & investment tracker for Android. Track expenses by
category, set monthly budgets, manage loan balances, and see a monthly spending dashboard —
all stored locally on the device with no network, accounts, or cloud sync.

> See [`docs/PRD.md`](docs/PRD.md) for product requirements and
> [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the technical design.

## Features

- **Expenses** — add, edit, delete, and filter expenses by date range.
- **Categories** — six predefined categories (Food, Transport, Entertainment, Utilities,
  Healthcare, Other) seeded automatically on first launch, plus user-defined categories.
- **Budgets** — set monthly spending limits per category.
- **Loans** — track loans and update their outstanding balances.
- **Dashboard** — total spending for the current month, tappable to open the expense list.
- **Theming** — Material 3 with automatic light/dark mode.

Currency is displayed as `₹` throughout (single currency per app instance).

## Technology Stack

| Area | Choice |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Persistence | Room (SQLite) |
| Async | Kotlin Coroutines + Flow |
| DI | Hilt |
| Architecture | Clean Architecture + MVVM |
| Min / Target / Compile SDK | 21 / 35 / 35 |

## Architecture

Three layers with strict dependency direction (presentation → domain ← data):

```
presentation/   Compose screens, ViewModels (StateFlow), navigation, theme
  ├── screen/        Compose screens
  ├── viewmodel/     @HiltViewModel classes exposing StateFlow<UiState>
  ├── component/     Reusable UI (ExpenseItem, LoanCard, CategorySelector, ErrorDialog)
  ├── navigation/    NavGraph
  └── theme/         Color, Type, Theme
domain/         Pure Kotlin — no Android/Room imports
  ├── model/         Data classes (Expense, ExpenseCategory, Budget, Loan)
  ├── repository/    Interfaces (I-prefixed)
  └── usecase/       Business logic + validation
data/           Room implementation details
  ├── model/         @Entity classes
  ├── local/dao/     Room DAOs
  ├── local/database/ FinanceDatabase
  └── local/repository/ Repository implementations (entity ↔ domain transforms)
di/             Hilt modules (DatabaseModule, RepositoryModule, UseCaseModule)
```

- **Repositories**: interfaces live in `domain`, implementations in `data`.
- **DI**: the database is an application-scoped singleton; ViewModels inject use cases, never DAOs.
- **Validation**: enforced in use cases (amount > 0, budget limit > 0, no future dates, required fields).
- **App entry point**: `FinanceApplication` (`@HiltAndroidApp`) seeds predefined categories on startup;
  `MainActivity` is `@AndroidEntryPoint`.

## Prerequisites

- **JDK 17** is required. The project uses Android Gradle Plugin 8.2.0, whose `jlink` image
  transform is incompatible with JDK 21+. Newer/older JDKs will fail the build.
- **Android SDK** (compile/target SDK 35) with `ANDROID_HOME` configured (or `local.properties`
  with `sdk.dir=...`).
- The Gradle wrapper (`./gradlew`, Gradle 8.7) is committed — no separate Gradle install needed.

## Build & Run

Set a JDK 17 as `JAVA_HOME` first, e.g.:

```bash
export JAVA_HOME=/path/to/jdk-17
export ANDROID_HOME=/path/to/android-sdk
export PATH="$JAVA_HOME/bin:$PATH"
```

Common commands:

```bash
./gradlew assembleDebug      # Build a debug APK
./gradlew installDebug       # Install onto a connected device/emulator
./gradlew assembleRelease    # Build a release APK
./gradlew test               # Run all unit tests
./gradlew lint               # Android lint
./gradlew ktlintFormat       # Format Kotlin
```

The debug APK is written to:

```
app/build/outputs/apk/expense_tracker.apk
```

Install it by copying to a device and tapping it (enable "install from unknown sources"), or via
`adb install app/build/outputs/apk/expense_tracker.apk`.

### Network/TLS note for restricted environments

If dependency downloads fail with an SSL handshake error behind a TLS-inspecting proxy, point
Gradle at the system truststore:

```bash
./gradlew build \
  -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts \
  -Djavax.net.ssl.trustStorePassword=changeit
```

## Testing

Unit tests cover the domain use cases (expense, category, budget, loan, dashboard) using JUnit 4,
Mockito, and `kotlinx-coroutines-test`. Per project convention, all unit tests use mocked
dependencies — no real database.

```bash
./gradlew test
```

The HTML report is generated at `app/build/reports/tests/testDebugUnitTest/index.html`.

UI is validated manually on a device/emulator (no instrumentation suite).

## Constraints

- Offline only — no network calls, cloud sync, or multi-user support.
- Single currency per app instance.
- Repository interfaces are `I`-prefixed; the domain layer never imports Room/Android.
