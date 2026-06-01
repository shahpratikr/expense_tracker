# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Personal Finance & Investment Tracker: offline-first Android app for expense/budget/loan tracking. See @docs/PRD.md (features) and @docs/ARCHITECTURE.md (technical design).

## Technology Stack

Kotlin, Jetpack Compose, Room (SQLite), Kotlin Coroutines, Hilt, MVVM, Android API 35.

## Build & Test Commands

```bash
./gradlew build                    # Build the app
./gradlew assembleRelease          # Build release APK
./gradlew test                     # Run all unit tests
./gradlew test --tests "path.Class" # Run specific test
./gradlew installDebug             # Deploy to emulator/device
./gradlew connectedAndroidTest     # Run instrumentation tests
./gradlew ktlintFormat             # Format Kotlin code
./gradlew lint                     # Check Android lint
```

## Architecture: Clean Architecture Three Layers

**Presentation** (`app/src/main/java/com/example/expense_tracker/presentation/`): Compose screens, ViewModels with StateFlow, NavGraph navigation.

**Domain** (`app/src/main/java/com/example/expense_tracker/domain/`): Pure Kotlin models, repository interfaces, use cases. No Android/Room imports.

**Data** (`app/src/main/java/com/example/expense_tracker/data/`): Room entities, DAOs, repository implementations, entity-to-model transforms.

## Folder Conventions

```
presentation/
├── screen/      # Compose screens
├── viewmodel/   # @HiltViewModel classes
├── component/   # Reusable UI components
└── navigation/  # NavGraph
domain/
├── model/       # Data classes
├── repository/  # Interfaces (I-prefix)
└── usecase/     # Business logic
data/
├── local/dao/   # Room DAOs
├── local/database/  # FinanceDatabase
├── local/repository/ # Implementations
└── model/       # Room entities (@Entity)
```

## Key Patterns

**Repository**: Interfaces in domain, implementations in data. Transforms Room entities ↔ domain models.

**DI (Hilt)**: Database is application-scoped singleton. All @HiltViewModel inject use cases, never DAOs directly.

**ViewModels**: Expose immutable `StateFlow<UiState>`. Public functions for user actions, delegate to use cases. Catch exceptions, expose errors in state.

**Validation**: Domain layer (use cases). Rules: amount > 0, budget limit > 0, required fields, valid dates.

**Offline**: SQLite only, no network layer, no cloud sync.

## Hard Constraints

- Never import Room in domain layer
- No cloud sync, network calls, or multi-user support
- Single currency per app instance
- Repository interfaces must have I-prefix (IExpenseRepository)
- All tests use mocked dependencies, no real database in unit tests

## Data Models

See @docs/ARCHITECTURE.md#core-data-models for full schema. Tables: expense_categories, expenses, budgets, loans.

## Testing

Domain: mock repositories. Repository: mock DAOs. ViewModel: mock use cases. UI: manual only.

## Development Phases

See @docs/ARCHITECTURE.md#development-phases. Order: database foundation, expense tracking, budgets, loans, dashboard & polish.
