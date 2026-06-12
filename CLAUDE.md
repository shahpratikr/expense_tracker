# Personal Finance & Investment Tracker
@docs/PRD.md
@docs/ARCHITECTURE.md

## Commands
```bash
./gradlew build                    # Build the app
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ANDROID_HOME=/home/pratik/Android/Sdk ./gradlew test --no-daemon
./gradlew test --tests "path.Class" # Run specific test
./gradlew installDebug             # Deploy to emulator/device
./gradlew ktlintFormat             # Format Kotlin code
./gradlew lint                     # Check Android lint
```

## Conventions
- Repository interfaces: I-prefix required (IExpenseRepository, not ExpenseRepository)
- ViewModels expose immutable StateFlow<UiState>, never expose raw values
- All @HiltViewModel inject use cases, never DAOs directly
- Database is application-scoped singleton via Hilt
- LlmInferenceHelper is application-scoped singleton; lifecycle managed by LoanViewModel
- Input validation in domain use cases: amount > 0, balance >= 0, required fields, valid dates
- Entity ↔ domain model transforms in data layer repositories
- Error handling: repositories throw; ViewModels catch and expose via state
- Presentation/Domain/Data layer separation — no Room imports in domain

## Constraints
- Never import Room in domain layer
- No cloud sync, network calls, or multi-user support
- Single currency per app (₹ INR fixed at build time)
- LLM used exclusively in Loans screen; no AI elsewhere
- All unit tests use mocked dependencies, no real database
- Loans are liabilities only; no receivables
